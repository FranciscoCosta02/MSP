import React, { useMemo,useEffect,useState } from 'react';
import {apiToken} from '../../../services/api/api';
import { MaterialReactTable } from 'material-react-table';
import RefreshIcon from '@mui/icons-material/Refresh';
import {
  IconButton,
  Tooltip,
  Stack,
} from '@mui/material';

export const EstatisticaAcessos = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [isError, setIsError] = useState(false);
  const [data, setData] = useState([]);

  const columns = useMemo(
    //column definitions...
    () => [
      {
        accessorKey: 'username',
        header: 'month',
      },
      {
        accessorKey: 'nLogins',
        header: 'Successful logins',
      },
      {
        accessorKey: 'nFails',
        header: 'Unsuccessful logins',
      },
      {
        accessorKey: 'firstLogin',
        header: '1º Login',
      },
      {
        accessorKey: 'lastLogin',
        header: 'Last login',
      },
      {
        accessorKey: 'lastAttempt',
        header: 'Last attempt',
      },
    ],
    [],
    //end
  );

  useEffect(() => {
    const fetchData = async () => {

      getData();
    
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
  ]);

  

  async function getData(){
    if (!data.length) {
      setIsLoading(true);
    } else {
      setIsRefetching(true);
    }

    try {
      const {data} = await apiToken.get('/statistics');
      console.log(data);
      setData(data);
    } catch (error) {
      setIsError(true);
      console.error("Error: "+error);
      return;
    }
    setIsError(false);
    setIsLoading(false);
    setIsRefetching(false);
  }

  return (
    <>
    
    <MaterialReactTable
      columns={columns}
      data={data}
      enableColumnActions={false}
      enableColumnFilters={false}
      enablePagination={false}
      enableSorting={false}
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
      enableBottomToolbar={false}
      muiTableBodyRowProps={{ hover: false }}
      state={{
        isLoading,
        showAlertBanner: isError,
        showProgressBars: isRefetching,
      }}
    />
    </>
  );
};

export default EstatisticaAcessos;
