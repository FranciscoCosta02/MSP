import React, { useCallback, useMemo, useState,useEffect } from 'react';
import {
  Box,
  Button,
  IconButton,
  Stack,
  Tooltip,
} from '@mui/material';
import { Delete, Edit } from '@mui/icons-material';
import {apiToken} from '../../../services/api/api';
import vars, { path } from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import MaterialReactTable from 'material-react-table';
import RefreshIcon from '@mui/icons-material/Refresh';
import PhotoAlbumIcon from '@mui/icons-material/PhotoAlbum';
import AlertComponent from '../../alerts/AlertComponent';
import CreateActivity from './CreateActivity';
import EditActivity from './EditActivity';
import {Link} from "react-router-dom";


const ListActivities = () => {
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);

  const [tableData, setTableData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  const [globalFilter, setGlobalFilter] = useState('');
  const [row, setRow] = useState({});
  const [index, setIndex] = useState(0);


  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });

  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };

  const handlCloseCreateAct = () => {
    setCreateModalOpen(false);
  }

  const handleCreateNewRow = (values) => {
    tableData.push(values);
    setTableData([...tableData]);
    setRowCount(rowCount+1);
    successAlert();
  };

  const handleEditRow = (index,values) => {
    tableData[index] = values;
    setTableData([...tableData]);
    handleEditModalClose();
  }

  async function getData(){
    if (!tableData.length) {
      setIsLoading(true);
    } else {
      setIsRefetching(true);
    }
    try {
      const {data} = await apiToken.get('/activity/backOffice?elements='+pagination.pageSize+'&page='+pagination.pageIndex+'&pattern='+globalFilter ?? '');
      console.log(data);
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

  function errorAlert(error){
    setOpen({open:true,type:"error",text:error});
    console.log(error);
  }

  function successAlert(){
    setOpen({open:true,type:"success",text:vars.alerts.activities.success});
  }

  /*const handleSaveRowEdits = async ({ exitEditingMode, row, values }) => {
      try{
        const {data} = await apiToken.put('/faq',values);
        console.log(data);
        tableData[row.index] = values;
        setTableData([...tableData]);
        successAlert();
        exitEditingMode(); //required to exit editing mode and close modal
      }catch(error){
        errorAlert(error.response.data);
      }
  };*/

  const handleCancelRowEdits = () => {
    
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteRow(row){
    try{
      const {data} = await apiToken.delete('/activity/'+row.id);
      console.log(data);
      tableData.splice(row.index, 1);
      setTableData([...tableData]);
      setRowCount(rowCount-1);
      successAlert();
    }catch(error){
      errorAlert(error.response.data)
    }
  }

  function handleEditModalClose(){
    setEditModalOpen(false);
  }

  const handleDeleteRow = useCallback(
    (row) => {
      console.log("handleDeleteRow");
      console.log(row);
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to delete the question: ${row.getValue('id')}`)
      ) {
        return;
      }
      
      deleteRow(row);
      
    },
    [deleteRow],
  ); 

  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
        enableEditing:false,
        enableHiding: false,
      },
      {
        accessorKey: 'title',
        header: 'Title',
        enableEditing:false,
        enableClickToCopy: true,
      },
      {
        accessorKey: 'description',
        header: 'Description',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'startDate',
        header: 'Start',
        enableEditing:false,
      },
      {
        accessorKey: 'endDate',
        header: 'End',
        enableEditing:false,
      },
      {
        accessorKey: 'username',
        header: 'Username',
        enableEditing:false,
      },
      {
        accessorKey: 'maxParticipants',
        header: 'maxParticipants',
        enableEditing:false,
      },
      {
        accessorKey: 'numParticipants',
        header: 'numParticipants',
        enableEditing:false,
      },

    ],
    [],
  );

  return (
    <>
      <MaterialReactTable
        columns={columns}
        data={tableData}
        initialState={{ columnVisibility: { id:false} }}
        enableColumnActions={false}
        enableColumnFilters={false}
        manualPagination
        enableSorting={false}
        getRowId={(row) => row.id}
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
        //onEditingRowSave={handleSaveRowEdits}
        onEditingRowCancel={handleCancelRowEdits}
        renderRowActions={({ row, table }) => (
          <Box sx={{ display: 'flex', gap: '1rem' }}>
            {
              validate.isUserAuthorized(vars.activities.edit) &&
              <Tooltip arrow placement="left" title="Edit">    
                  <IconButton onClick={() => {
                    setRow(row.original)
                    setIndex(row.index)
                    setEditModalOpen(true)
                  }}>
                    <Edit />
                  </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.activities.remove) &&
              <Tooltip arrow placement="right" title="Delete">
                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                  <Delete />
                </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.activities.photos) &&
            <Tooltip title="Photos">
              <Link to={path.photos+"/"+row.original.id+"/"+row.original.title}>
                <IconButton
                  color="success"
                >
                  <PhotoAlbumIcon />
                </IconButton>
              </Link>
            </Tooltip>
            }
            
          </Box>
        )}
        renderTopToolbarCustomActions={() => (
          <Stack direction="row" spacing={2} >
            <Button
              onClick={() => setCreateModalOpen(true)}
              variant="contained"
              disabled={!validate.isUserAuthorized(vars.activities.create)}
            >
                Create New Activity              
            </Button>

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

      <EditActivity
        open={editModalOpen}
        onClose={handleEditModalClose}
        row={row}
        onSubmit={handleEditRow}
        errorAlert={errorAlert}
        index={index}
      />

      <CreateActivity
        open={createModalOpen}
        onClose={handlCloseCreateAct}
        errorAlert={errorAlert}
        onSubmit={handleCreateNewRow}
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


export default ListActivities;


