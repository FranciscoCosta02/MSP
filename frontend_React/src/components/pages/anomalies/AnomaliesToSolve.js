import React, { useEffect, useMemo, useState,useCallback } from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';
import {
  Box,
  Button,
  IconButton,
  Stack,
  Tooltip,
} from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import RefreshIcon from '@mui/icons-material/Refresh';
import CloseIcon from '@mui/icons-material/Close';
import vars from '../../../services/var/var';
import AlertComponent from '../../alerts/AlertComponent';
import { CreateAnomalyModal } from './modal/CreateAnomalyModal';

const AnomaliesTable = () => {
  //data and fetching state
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);

  

  //table state
  const [globalFilter, setGlobalFilter] = useState('');
  const [sorting, setSorting] = useState([]);
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });

  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };

  async function getData(){
    if (!data.length) {
      setIsLoading(true);
    } else {
      setIsRefetching(true);
    }

    try {
      const {data} = await apiToken.get('/anomaly?solved=false&page='+pagination.pageIndex+'&elements='+pagination.pageSize);
      console.log(data)
      setData(JSON.parse(data.list));
      setRowCount(data.maxNumber);
    } catch (error) {
      setIsError(true);
      console.error("Error: "+error);
      return;
    }
    setIsError(false);
    setIsLoading(false);
    setIsRefetching(false);
  };

  //if you want to avoid useEffect, look at the React Query example instead
  useEffect(() => {
    
    getData();
    //eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    globalFilter,
    pagination.pageIndex,
    pagination.pageSize,
    sorting,
  ]);

  const handleSolveAnomaly = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want solve the anomaly : ${row.getValue('id')}`)
      ) {
        return;
      }
      solveAnomalies(row);
      
    },
    [solveAnomalies],
  ); 

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function solveAnomalies(anomaly){
    console.log("RESOLVER ANOMALIA: ");
    console.log(anomaly);
    try{
      const {dataAPI} = await apiToken.post('/anomaly/solve?id='+anomaly.id+"&solved=true");
      console.log(dataAPI);
      openSuccessAlert(anomaly.index);
      setRowCount(rowCount-1);
    }
    catch(error){
      openErrorAlert(error);
    }
  }

  const handleDeleteAnomaly = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to delete the anomaly : ${row.getValue('id')}`)
      ) {
        return;
      }
      deleteAnomalies(row);
    },
    [deleteAnomalies],
  ); 

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteAnomalies(anomaly){
    console.log("DELETE ANOMALIA: ");
    console.log(anomaly);
    try{
      await apiToken.delete('/anomaly?aid='+anomaly.id);
      openSuccessAlert(anomaly.index);
      setRowCount(rowCount-1);
    }
    catch(error){
      openErrorAlert(error);
    }
  }

  function openSuccessAlert(index){
    setOpen({open:true,type:"success",text:vars.alerts.success});
    data.splice(index,1); //assuming simple data table
    setData([...data]);
  }

  function openErrorAlert(error){
    setOpen({open:true,type:"error",text:vars.alerts.error});
    console.log(error);

  }

  const handleCreateNewRow = (values) => {
    data.push(values);
    setData([...data]);
    setRowCount(rowCount+1);
  };

  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
      },
      //column definitions...
      {
        accessorKey: 'reason',
        header: 'Reason',
      },
      {
        accessorKey: 'text',
        header: 'Text',
      },
      {
        accessorKey: 'formattedTime',
        header: 'Created at',
      },
      {
        accessorKey: 'sender',
        header: 'Sender',
      },
      {
        accessorKey: 'solved',
        header: 'solved',
        enableHiding: false,
      },
      
      
      //end
    ],
    [],
  );


  return (
    <>
    
    <MaterialReactTable
      columns={columns}
      data={data}
      getRowId={(row) => row.id}
      initialState={{ columnVisibility: { solved:false,id:false } }}
      manualFiltering
      manualPagination
      manualSorting
      muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading anomalies',
            }
          : undefined
      }
      renderTopToolbarCustomActions={() => (
        <Stack direction="row" spacing={2} >
          <Button
            onClick={() => setCreateModalOpen(true)}
            variant="contained"
          >
            Create new Anomaly
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
      enableRowActions
      renderRowActions={({ row, table }) => (
        <Box sx={{ display: 'flex', flexWrap: 'nowrap', gap: '8px' }}>
          <Tooltip title="solve">
            <IconButton
              color="success"
              onClick={() => {
                handleSolveAnomaly(row);
              }}
            >
              <CheckCircleOutlineIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="delete">
            <IconButton
              color="error"
              onClick={() => {
                handleDeleteAnomaly(row);
                
              }}
            >
              <CloseIcon/>
            </IconButton>
          </Tooltip>
        </Box>
      )}
      onGlobalFilterChange={setGlobalFilter}
      onPaginationChange={setPagination}
      onSortingChange={setSorting}
      rowCount={rowCount+1}
      state={{
        globalFilter,
        isLoading,
        pagination,
        showAlertBanner: isError,
        showProgressBars: isRefetching,
        sorting,
      }}
    />
    <AlertComponent
      openModal={open.open}
      type={open.type}
      text={open.text}
      onClose={handleClose}
    />

    <CreateAnomalyModal
      columns={columns}
      open={createModalOpen}
      onClose={() => setCreateModalOpen(false)}
      onSubmit={handleCreateNewRow}
    />

    </>
  );
};


export default AnomaliesTable;
