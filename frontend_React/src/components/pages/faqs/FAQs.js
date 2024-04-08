import React, { useCallback, useMemo, useState,useEffect } from 'react';
import {
  Box,
  Button,
  IconButton,
  Stack,
  Tooltip
} from '@mui/material';
import { Delete, Edit } from '@mui/icons-material';
import {apiToken} from '../../../services/api/api';
import vars from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import MaterialReactTable from 'material-react-table';
import RefreshIcon from '@mui/icons-material/Refresh';
import AlertComponent from '../../alerts/AlertComponent';
import { CreateFAQsModal } from './CreateFAQsModal';


const FAQs = () => {
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [tableData, setTableData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  const [globalFilter, setGlobalFilter] = useState('');

  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 5,
  });

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
      const {data} = await apiToken.get('/list/faqs?elements='+pagination.pageSize+'&page='+pagination.pageIndex+'&pattern='+globalFilter ?? '');
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
    setOpen({open:true,type:"error",text:vars.alerts.error});
    console.log(error);
  }

  function successAlert(){
    setOpen({open:true,type:"success",text:vars.alerts.success});
  }

  const handleSaveRowEdits = async ({ exitEditingMode, row, values }) => {
      try{
        const {data} = await apiToken.put('/faq',values);
        console.log(data);
        tableData[row.index] = values;
        setTableData([...tableData]);
        successAlert();
        exitEditingMode(); //required to exit editing mode and close modal
      }catch(error){
        errorAlert(error);
      }
  };

  const handleCancelRowEdits = () => {
    
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteRow(row){
    try{
      console.log("ROW: ");
      console.log(row);
      const {data} = await apiToken.delete('/faq?id='+row.id);
      console.log(data);
      //send api delete request here, then refetch or update local table data for re-render
      tableData.splice(row.index, 1);
      setTableData([...tableData]);
      setRowCount(rowCount-1);
      successAlert();
    }catch(error){
      errorAlert(error)
    }
  }

  const handleDeleteRow = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to delete the question: ${row.getValue('question')}`)
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
        accessorKey: 'question',
        header: 'Questions',
        enableEditing:false,
      },
      {
        accessorKey: 'answer',
        header: 'Answers',
      },
      {
        accessorKey: 'tag',
        header: 'Tag',
        editVariant:'select',
        editSelectOptions: vars.mock.faqTagList,
      },

    ],
    [],
  );

  return (
    <>
      <MaterialReactTable
        columns={columns}
        data={tableData}
        initialState={{}} 
        enableColumnActions={false}
        enableColumnFilters={false}
        manualPagination
        enableSorting={false}
        getRowId={(row) => row.question}
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
        onEditingRowSave={handleSaveRowEdits}
        onEditingRowCancel={handleCancelRowEdits}
        renderRowActions={({ row, table }) => (
          <Box sx={{ display: 'flex', gap: '1rem' }}>
            {
              validate.isUserAuthorized(vars.faqsAuth.edit) &&
              <Tooltip arrow placement="left" title="Edit">
                <IconButton onClick={() => table.setEditingRow(row)}>
                  <Edit />
                </IconButton>
              </Tooltip>
            }
            {
              validate.isUserAuthorized(vars.faqsAuth.remove) &&
              <Tooltip arrow placement="right" title="Delete">
                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                  <Delete />
                </IconButton>
              </Tooltip>
            }
            
          </Box>
        )}
        renderTopToolbarCustomActions={() => (
          <Stack direction="row" spacing={2} >
            <Button
              onClick={() => setCreateModalOpen(true)}
              variant="contained"
              disabled={!validate.isUserAuthorized(vars.faqsAuth.create)}
            >
              Create New Question
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
      <CreateFAQsModal
        columns={columns}
        open={createModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onSubmit={handleCreateNewRow}
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


export default FAQs;


