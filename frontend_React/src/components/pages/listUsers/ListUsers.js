import React, { useEffect, useMemo, useState ,useCallback} from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';
import vars, { u } from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import RefreshIcon from '@mui/icons-material/Refresh';
import {
    Edit as EditIcon,
    Delete as DeleteIcon,
    Badge as BadgeIcon,
    Check as CheckIcon,
    Block as BlockIcon,
} from '@mui/icons-material';
import {
  Box,
  IconButton,
  Tooltip,
} from '@mui/material';
import AlertComponent from '../../alerts/AlertComponent';
import { ChangeUserRoleModal } from './modal/ChangeUserRoleModal';
import { DeleteUserModal } from './modal/DeleteUserModal';




const Table = (props) => {
  //data and fetching state
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  
  //Modal
  const [changeRoleModalOpen, setChangeRoleModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [modalInfo, setModalInfo] = useState({row:{},myindex:0,userToDelete:""});

  //table state
  const [globalFilter, setGlobalFilter] = useState('');
  const [sorting, setSorting] = useState([]);
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 5,
  });  
  //const [userType, setUserType] = useState("");

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
        console.log("getData()");
        console.log("getData()");

        const {data} = await apiToken.get('/list/backOffice/'+props.userType+'?elements='+pagination.pageSize+'&page='+pagination.pageIndex+'&pattern='+globalFilter ?? '');
        console.log(data);
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    props.userType,
    globalFilter,
    pagination.pageIndex,
    pagination.pageSize,
    sorting,
  ]);


  const columns = useMemo(
    () => [
      {
        accessorKey: 'username',
        header: 'Username',
        enableHiding: false,
        enableEditing:false,
        
      },
      //column definitions...
      {
        accessorKey: 'name',
        header: 'Name',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'email',
        header: 'Email',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'phone',
        header: 'Phone',
        enableClickToCopy: true,
      },
      {
        accessorKey: 'department',
        header: 'Department',
        editVariant:'select',
        editSelectOptions: vars.mock.departmentsLogin,
      },
      {
        accessorKey: 'role',
        header: 'Role',
        enableEditing:false,
      },
      {
        accessorKey: 'activity',
        header: 'Activity',
        enableEditing:false,
      },

    ],
    [],
  );


  const handleSaveRow = async ({ exitEditingMode, row, values }) => {
    //if using flat data and simple accessorKeys/ids, you can just do a simple assignment here.
    var tmp = data[row.index];
    data[row.index] = values;
    //send/receive api updates here
    try{
      const {data} = await apiToken.put('/update/attributes',values);
      console.log(data);
    }
    catch(Error){
      data[row.index]=tmp;
      console.log(Error);
    }
    
    setData([...data]);
    exitEditingMode(); //required to exit editing mode
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteRow(index,username){
    try{
      console.log("DELETE ROW");
      await apiToken.delete('/delete?id='+username);
      deleteDataSuccess(index);
    }catch(error){
      errorAlert(error);
    }
  }

  function deleteDataSuccess(index){
    setOpen({open:true,type:"success",text:vars.alerts.success});
    data.splice(index, 1);
    setData([...data]);
    setRowCount(rowCount-1);
  }

  function errorAlert(error){
    setOpen({open:true,type:"error",text:vars.alerts.error});
    console.log(error);
  }

  function updateDataSuccess(index,row){
    setOpen({open:true,type:"success",text:vars.alerts.success});
    data[index] = row;
    setData([...data]);
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function activateAccount(username,index,row){
    console.log("activateAccount");
    console.log(username)
    row.activity=vars.active;
    
    try{
      await apiToken.put('/update/activate?id='+username);
      updateDataSuccess(index,row);
    }catch(error){
      errorAlert(error);
    }
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deactivateAccount(username,index,row){
    console.log("deactivateAccount");
    console.log(username)
    row.activity=vars.inactive;
    
    try{
      await apiToken.put('/update/deactivate?id='+username);
      updateDataSuccess(index,row);
    }catch(error){
      errorAlert(error);
    }
  }

  const handleActivityUserAccount = useCallback(
    (row,state) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want change de activity state to `+state+` on ${row.getValue('username')}'s account`)
      ) {
        return;
      }
      if(state===vars.active){
        activateAccount(row.getValue('username'),row.index,row.original);
      }
      else{
        deactivateAccount(row.getValue('username'),row.index,row.original);
      }
      
    },
    [activateAccount,deactivateAccount],
  ); 


  async function updateRole(index,username,newRole,newRow){
    newRow.role=newRole;
    try{
      await apiToken.put('/update/role',{"username":username,"role":newRole});
      updateDataSuccess(index,newRow);
    }catch(error){
      errorAlert(error);
    }
  }


  return (
    <>
    
    <MaterialReactTable
      columns={columns}
      data={data}
      getRowId={(row) => row.username}
      initialState={{ columnVisibility: { role:false,groups: false,activity:false,privacy:false} }}
      manualFiltering
      manualPagination
      manualSorting
      onEditingRowSave={handleSaveRow}
      muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading anomalies',
            }
          : undefined
      }
      enableEditing={true}
      enableRowActions
      renderTopToolbarCustomActions={({ table }) => (
        <>
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
        </>
      )}
      renderRowActions={({ row, table }) => (
        <Box sx={{ display: 'flex', flexWrap: 'nowrap', gap: '8px' }}>
          {
            validate.isUserAuthorized(vars.listUsersAuth.edit) &&
            <Tooltip title="Edit">
            <IconButton
              color="black"
              onClick={() => {
                table.setEditingRow(row);
              }}
            >
              <EditIcon />
            </IconButton>
            </Tooltip>
          }
          {
            row.getValue('role')!==u.superUser &&
            <>
            {
              validate.isUserAuthorized(vars.listUsersAuth.delete) &&
              <Tooltip title="Delete">
                <IconButton
                  color="error"
                  onClick={() => {
                    setDeleteModalOpen(true);
                    setModalInfo({row:row,userToDelete:row.getValue('username'),myindex:row.index})
                  }}
                >
                  <DeleteIcon />
                </IconButton>
              </Tooltip>
            }
          
            {
              validate.isUserAuthorized(vars.listUsersAuth.editrole) &&
              <Tooltip title="Change role">
                <IconButton
                  color="warning"
                  onClick={() => {
                    setChangeRoleModalOpen(true)
                    setModalInfo({row:row.original,userToDelete:"",myindex:row.index})
                  }}
                >
                  <BadgeIcon />
                </IconButton>
              </Tooltip>
            }
            
            {  validate.isUserAuthorized(vars.listUsersAuth.activateAccount) && row.getValue('activity')===vars.inactive &&
              <Tooltip title="activate account">
                <IconButton
                  color="primary"
                  onClick={() => {
                    handleActivityUserAccount(row,vars.active);
                  }}
                >
                  <CheckIcon />
                </IconButton>
              </Tooltip>
            }
            {  validate.isUserAuthorized(vars.listUsersAuth.inactivateAccount) && row.getValue('activity')===vars.active &&
              <Tooltip title="deactivate account">
                <IconButton
                  color="primary"
                  onClick={() => {
                    handleActivityUserAccount(row,vars.inactive);
                  }}
                >
                  <BlockIcon />
                </IconButton>
              </Tooltip>
            }
            </>
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

    <ChangeUserRoleModal
        open={changeRoleModalOpen}
        onClose={() => setChangeRoleModalOpen(false)}
        onSubmit={updateRole}
        row={modalInfo.row}
        index={modalInfo.myindex}
      /> 

    <DeleteUserModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={deleteRow}
        row={modalInfo.row}
        username={modalInfo.userToDelete}
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


export default Table;
