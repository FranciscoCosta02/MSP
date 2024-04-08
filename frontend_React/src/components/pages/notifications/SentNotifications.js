import React, { useEffect, useMemo, useState } from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';


const SentNotifications = () => {
  //data and fetching state
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);

  //table state
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });

  //if you want to avoid useEffect, look at the React Query example instead
  useEffect(() => {
    const fetchData = async () => {
      if (!data.length) {
        setIsLoading(true);
      } else {
        setIsRefetching(true);
      }

      try {
        const {data} = await apiToken.get('/list/sent?elements='+pagination.pageSize+'&page='+pagination.pageIndex);
        console.log(data);
        if(data.length===0 || data.maxNumber===0){
          setData([]);
          setRowCount(0);
        }
        else{
          setData(JSON.parse(data.list));
          setRowCount(data.maxNumber);
        }
        
      } catch (error) {
        setIsError(true);
        console.error(error);
        return;
      }
      setIsError(false);
      setIsLoading(false);
      setIsRefetching(false);
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    pagination.pageIndex,
    pagination.pageSize,
  ]);

  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
      },
      {
        accessorKey: 'dest',
        header: 'Destination',
      },
      //column definitions...
      {
        accessorKey: 'text',
        header: 'Text',
      },
      //end
    ],
    [],
  );

  return (
    <MaterialReactTable
      columns={columns}
      data={data}
      getRowId={(row) => row.id}
      manualPagination
      muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading data',
            }
          : undefined
      }
      onPaginationChange={setPagination}
      rowCount={rowCount+1}
      state={{
        isLoading,
        pagination,
        showAlertBanner: isError,
        showProgressBars: isRefetching,
      }}
    />
  );
};

export default SentNotifications;
