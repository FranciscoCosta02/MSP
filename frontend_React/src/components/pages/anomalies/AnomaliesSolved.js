import React, { useEffect, useMemo, useState,useCallback } from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';
import vars from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import { Box, IconButton } from '@mui/material';
import Tooltip from '@mui/material/Tooltip';
import SettingsBackupRestoreIcon from '@mui/icons-material/SettingsBackupRestore';
import RefreshIcon from '@mui/icons-material/Refresh';
import AlertComponent from '../../alerts/AlertComponent';


const AnomaliesTable = () => {
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
      const {data} = await apiToken.get('/anomaly?solved=true&page='+pagination.pageIndex+'&elements='+pagination.pageSize);
      console.log(JSON.parse(data.list))
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
  }

  //if you want to avoid useEffect, look at the React Query example instead
  useEffect(() => {
    getData();
    //eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    globalFilter,
    pagination.pageIndex,
    pagination.pageSize,
    sorting
  ]);

  const handlePutToSolveAnomaly = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to put the anomaly to be solved? : ${row.getValue('id')}`)
      ) {
        return;
      }
      putToSolveAnomalies(row);
    },
    [putToSolveAnomalies],
  ); 

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function putToSolveAnomalies(anomaly){
    console.log("RESOLVER ANOMALIA: ");
    console.log(anomaly);

    setIsRefetching(true);

    try{
      const {dataAPI} = await apiToken.post('/anomaly/solve?id='+anomaly.id+"&solved=false");
      console.log(dataAPI);
      data.splice(anomaly.index,1); //assuming simple data table
      setOpen({open:true,type:"success",text:vars.alerts.success});
      setData([...data]);
      setRowCount(rowCount-1);
    }
    catch(Error){
      console.log(Error);
      setOpen({open:true,type:"error",text:vars.alerts.error});
    }

    setIsRefetching(false);
    

  }

  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
      },
      //column definitions...
      {
        accessorKey: 'formattedTime',
        header: 'Created at',
      },
      {
        accessorKey: 'reason',
        header: 'Reason',
      },
      {
        accessorKey: 'text',
        header: 'Text',
      },
      {
        accessorKey: 'sender',
        header: 'Sender',
      },
      {
        accessorKey: 'solved',
        header: 'solved',
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
      initialState={{ columnVisibility: { solved:false } }}
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
      )}
      enableRowActions
      renderRowActions={({ row, table }) => (
        <Box sx={{ display: 'flex', flexWrap: 'nowrap', gap: '8px' }}>
        {
          validate.isUserAuthorized(vars.anomalies.puToSolve) &&
          <Tooltip title="put to solve">
            <IconButton
              color="warning"
              onClick={() => {
                handlePutToSolveAnomaly(row)
              }}
            >
              <SettingsBackupRestoreIcon/>
            </IconButton>
          </Tooltip>
        }
          
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

export default AnomaliesTable;
