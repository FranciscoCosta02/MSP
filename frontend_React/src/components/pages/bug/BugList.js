import React, { useEffect, useMemo, useState,useCallback } from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';
import {
  Box,
  IconButton,
  Stack,
  Tooltip,
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import CloseIcon from '@mui/icons-material/Close';
import vars from '../../../services/var/var';
import AlertComponent from '../../alerts/AlertComponent';

const BugList = () => {
  //data and fetching state
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
      const {data} = await apiToken.get('/bug?page='+pagination.pageIndex+'&elements='+pagination.pageSize);
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

  const handleDeleteBug = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to delete the anomaly : ${row.getValue('id')}`)
      ) {
        return;
      }
      deleteBug(row);
    },
    [deleteBug],
  ); 

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteBug(anomaly){
    console.log("DELETE BUG: ");
    console.log(anomaly);
    try{
      await apiToken.delete('/bug?aid='+anomaly.id);
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



  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
        enableHiding: false,
      },
      //column definitions...
      {
        accessorKey: 'reason',
        header: 'Reason',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'text',
        header: 'Text',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'sender',
        header: 'Sender',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'formattedTime',
        header: 'Created at',
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
      initialState={{ columnVisibility: { id:false } }}
      manualFiltering
      manualPagination
      manualSorting
      muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading reported bugs',
            }
          : undefined
      }
      renderTopToolbarCustomActions={() => (
        <Stack direction="row" spacing={2} >
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
          <Tooltip title="delete">
            <IconButton
              color="error"
              onClick={() => {
                handleDeleteBug(row);
                
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
    </>
  );
};


export default BugList;
