import React, { useCallback, useMemo, useState,useEffect } from 'react';
import {
  Box,
  Button,
  IconButton,
  Stack,
  Tooltip,
} from '@mui/material';

import { Delete, Edit,Schedule } from '@mui/icons-material';
import {apiToken} from '../../../services/api/api';
import vars from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import MaterialReactTable from 'material-react-table';
import RefreshIcon from '@mui/icons-material/Refresh';
import AlertComponent from '../../alerts/AlertComponent';
import { AddNewRoomModal } from './modal/AddNewRoomModal';
import { MakeAnAppointmentModal } from './modal/MakeAnAppointmentModal';
import { EditRoomModal } from './modal/EditRoomModal';
import StickyNote2Icon from '@mui/icons-material/StickyNote2';
import { ReservationsListModal } from './modal/ReservationsListModal';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';

const MyRooms = () => {
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);

  const [row, setRow] = useState({index:-1,data:{weekDays:[]}});

  const [tableData, setTableData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  const [globalFilter, setGlobalFilter] = useState('');

  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });

  //make an appointment
  const [makeAnAppointmentOpen, setMakeAnAppointmentOpen] = useState({open:false,row:{}});
  const handleCloseAppointmentModal = () => {
    setMakeAnAppointmentOpen({open:false,row:{}});
  };

  //reservations list
  const [reservationsOpen, setReservationsOpen] = useState({open:false,row:{}});
  const handleCloseReservationsListModal = () => {
    setReservationsOpen({open:false,row:{}});
  };


  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };

  const handleCreateNewRow = (values) => {
    tableData.push(values);
    setTableData([...tableData]);
    setRowCount(rowCount+1);
    successAlert();
  };

  async function getData(){
    if (!tableData.length) {
      setIsLoading(true);
    } else {
      setIsRefetching(true);
    }
    try {
      const {data} = await apiToken.get('/list/backOffice/rooms?elements='+pagination.pageSize+'&page='+pagination.pageIndex+'&pattern='+globalFilter ?? '');
      console.log(JSON.parse(data.list));
      setTableData(JSON.parse(data.list));
      setRowCount(data.maxNumber);
    } catch (error) {
      setIsError(true);
      console.error(error);
      return;
    }
    setIsError(false);
    setIsLoading(false);
    setIsRefetching(false);
  }

  useEffect(() => {
    getData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    globalFilter,
    pagination.pageIndex,
    pagination.pageSize,
  ]);

  const handleCancelRowEdits = () => {
    
  };

  function errorAlert(error){
    setOpen({open:true,type:"error",text:error});
    console.log(error);
  }

  function successAlert(){
    setOpen({open:true,type:"success",text:vars.alerts.success});
  }

  async function handleSaveRowEdits(index, values){
    console.log(values);
    try{
      const {data} = await apiToken.put('/rooms',values);
      console.log(data);
      tableData[index] = values;
      setTableData([...tableData]);
      setEditModalOpen(false);
      successAlert();
    }catch(error){
      errorAlert(error.response.data);
    }
};

// eslint-disable-next-line react-hooks/exhaustive-deps
async function deleteRow(row,index){
  try{
    await apiToken.delete('/rooms',{ data: row });
    tableData.splice(index, 1);
    setTableData([...tableData]);
    setRowCount(rowCount-1);
    successAlert();
  }catch(error){
    errorAlert(error.response.data);
  }
}

const handleDeleteRow = useCallback(
  (row) => {
    console.log("row");
    if (
      // eslint-disable-next-line no-restricted-globals
      !confirm(`Are you sure you want to delete the room: ${row.getValue('name')} - ${row.getValue('department')}`)
    ) {
      return;
    }
    deleteRow(row.original,row.index);
  },
  [deleteRow],
);  


async function handleChangeAvailability(state){
  console.log(state)
  if (
    // eslint-disable-next-line no-restricted-globals
    !confirm(`Are you sure you want to turn all the rooms ${state}?`)
  ) {
    return;
  }
  try{
    const {data} = await apiToken.put('/rooms/allRooms?state='+state);
    console.log(data);
    successAlert();
  }
  catch(error){
    errorAlert(error.response.data);
  }
}

const columns = useMemo(
  () => [

    {
      accessorKey: 'availability',
      header: 'availability',
      enableHiding: false,
      /*Cell: ({ cell }) => (
        
        (cell.getValue()==="Available"?
        <>
            <Tooltip arrow title="Available">
              <IconButton color="success">
                <FiberManualRecordIcon/>
              </IconButton>
            </Tooltip>
        </>:
        <>
            <Tooltip arrow title="Unavailable">
              <IconButton color="error">
                  <FiberManualRecordIcon/>
              </IconButton>
            </Tooltip>
        </>)
                  
        
      ),*/
    },
    {
      accessorKey: 'name',
      header: 'Name',
      enableEditing:false,
    },
    {
      accessorKey: 'department',
      header: 'Department',
      enableEditing:false,
    },
    {
      accessorKey: 'openTime',
      header: 'Open at',
    },
    {
      accessorKey: 'closeTime',
      header: 'Close at',
    },
    
    {
      accessorKey: 'weekDays',
      header: 'week days',
      //editVariant:'select',
      //editSelectOptions:vars.mock.weekDay,
    },
    
  ],
  [],
);

async function handleAvailability(row,index){
  console.log(row)
  console.log(index)
  try{
    const {data} = await apiToken.put('/rooms/availability',row)
    console.log(data);
    row.availability=(row.availability==="Available"?"Unvailable":"Available")
    tableData[index]=row;
    setTableData([...tableData]);
    successAlert();
  }
  catch(error){
    errorAlert(error.response.data);
  }
}


  
  return (
    <>            
      <MaterialReactTable
        columns={columns}
        data={tableData}
        initialState={{columnVisibility: {availability:false}}} 
        enableColumnActions={false}
        enableColumnFilters={false}
        manualPagination
        enableSorting={false}
        getRowId={(row) => row.name}
        editingMode="modal" //default
        enableEditing
        muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading anomalies',
            }
          : undefined
      }
        onEditingRowCancel={handleCancelRowEdits}
        renderRowActions={({ row, table }) => (
          <Box sx={{ display: 'flex', gap: '1rem' }}>
            {
              validate.isUserAuthorized(vars.rooms.edit) &&
              <Tooltip arrow placement="left" title="Edit">
                <IconButton onClick={() => {
                  console.log("EDIT");
                  console.log(row);
                  setRow({index:row.index,data:row.original})
                  setEditModalOpen(true)}
                  }>
                  <Edit />
                </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.rooms.remove) &&
              <Tooltip arrow placement="right" title="Delete">
                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                  <Delete />
                </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.rooms.schedule) &&
              <Tooltip arrow placement="left" title="book room">
                <IconButton color="warning" onClick={() => setMakeAnAppointmentOpen({open:true,row:row.original})}>
                  <Schedule />
                </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.rooms.reservationsList) &&
              <Tooltip arrow placement="bottom" title="booking list">
                <IconButton color="info" onClick={() => setReservationsOpen({open:true,row:row.original})}>
                  <StickyNote2Icon />
                </IconButton>
              </Tooltip>
            }
              
            <Tooltip arrow title="Available">
              <IconButton color={row.getValue("availability")==="Available"?"success":"error"} 
              onClick={()=>handleAvailability(row.original,row.index)}>
                <FiberManualRecordIcon/>
              </IconButton>
            </Tooltip>
              
     
            
          </Box>
        )}
        renderTopToolbarCustomActions={() => (
          <Stack direction="row" spacing={2} >
            {
              validate.isUserAuthorized(vars.rooms.remove) &&
              <Button
                onClick={() => setCreateModalOpen(true)}
                variant="contained"
                disabled={!validate.isUserAuthorized(vars.faqsAuth.create)}
              >
                Add room
              </Button>
            }
            <Tooltip title="Refresh data">
              <IconButton
                color="primary"
                onClick={() => {
                  getData();
                }}
              >
                <RefreshIcon />
              </IconButton>
            </Tooltip>
            <Tooltip title="Turn ALL rooms available">
              <IconButton
                color="success"
                onClick={() => {
                  handleChangeAvailability("Available")
                }}
              >
                <EventAvailableIcon />
              </IconButton>
            </Tooltip>
            <Tooltip title="Turn ALL rooms unavailable">
              <IconButton
                color="error"
                onClick={() => {
                  handleChangeAvailability("Unavailable")
                }}
              >
                <EventBusyIcon />
              </IconButton>
            </Tooltip>
          </Stack>
        )}
        onGlobalFilterChange={setGlobalFilter}
        onPaginationChange={setPagination}
        rowCount={rowCount+1}
        state={{
        globalFilter,
        isLoading,
        pagination,
        showAlertBanner: isError,
        showProgressBars: isRefetching,
      }}
      />

      <AddNewRoomModal
        open={createModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onSubmit={handleCreateNewRow}
        errorAlert={errorAlert}

      />

      <EditRoomModal
        open={editModalOpen}
        onClose={() => setEditModalOpen(false)}
        row={row}
        onSubmit={handleSaveRowEdits}
        errorAlert={errorAlert}

      />

      <MakeAnAppointmentModal
        open={makeAnAppointmentOpen.open}
        row={makeAnAppointmentOpen.row}
        onClose={handleCloseAppointmentModal}
        successAlert={successAlert}
        errorAlert={errorAlert}
      />

      <ReservationsListModal
        open={reservationsOpen.open}
        row={reservationsOpen.row}
        onClose={handleCloseReservationsListModal}
        successAlert={successAlert}
        errorAlert={errorAlert}
      />
      
      <AlertComponent
        openModal={open.open}
        type={open.type}
        text={open.text}
        onClose={handleClose}
      />
    </>
  );
};

export default MyRooms;
