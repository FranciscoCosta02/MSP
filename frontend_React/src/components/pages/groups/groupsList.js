import React, { useEffect, useMemo, useState,useCallback } from 'react';
import MaterialReactTable from 'material-react-table';
import {apiToken} from '../../../services/api/api';
import FormatListBulletedIcon from '@mui/icons-material/FormatListBulleted';
import RefreshIcon from '@mui/icons-material/Refresh';
import DeleteIcon from '@mui/icons-material/Delete';
import AlertComponent from '../../alerts/AlertComponent';
import vars from '../../../services/var/var';
import validate from '../../../services/var/validationFunc';
import CreationGroupDialog from './Dialog/creationGroupDialog';
import GroupCreationModal from './modal/groupCreationModal';
import PasswordIcon from '@mui/icons-material/Password';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import {
  Box,
  Button,
  IconButton,
  Stack,
  Tooltip,
  Modal,
  Typography,
} from '@mui/material';
import ChangePassWordModal from './modal/ChangePassWordModal';
import SelectElementDialog from './Dialog/SelectElementDialog';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
};


const GroupsList = () => {
  //data and fetching state
  const [tableData, setTableData] = useState([]);
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefetching, setIsRefetching] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  const [globalFilter, setGlobalFilter] = useState('');

  //Participants List
  const [openParticipantsList, setOpenParticipantsList] = useState(false);
  const handleModalOpen = () => setOpenParticipantsList(true);
  const handleModalClose = () => setOpenParticipantsList(false);
  const [participantsList, setParticipantsLista] = useState([]);
  const [myRow, setMyRow] = useState({});
  const [title, setTitle] = useState("");

  //Dialog
  const [CreateDialogOpen, setCreateDialogOpen] = useState(false);

  //Modal
  const [openModal, setOpenModal] = useState(false);
  const [groupType, setGroupType] = useState("");

  //table state
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });

  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };

  const [openModalChangePwd, setOpenModalChangePwd] = useState(false);
  const handleOpenModalChangePwd = (row) => {
    setOpenModalChangePwd(true);
    console.log(row);
    setMyRow(row);
  };
  const handleCloseModalChangePwd = () => {
    setOpenModalChangePwd(false);
  };

  
  const [openRemoveParticipantDialog, setOpenRemoveParticipantDialog] = useState(false);
  const handleOpenRemoveParticipantDialog = (row) => {
    setOpenRemoveParticipantDialog(true);
    console.log(row);
    setMyRow(row);
    setTitle("Remove Participant")
  };
  const handleCloseRemoveParticipantDialog = () => {
    setOpenRemoveParticipantDialog(false);
  };

  const [openPromoteParticpantDialog, setOpenPromoteParticpantDialog] = useState(false);
  const handleOpenPromoteParticpantDialog = (row) => {
    setOpenPromoteParticpantDialog(true);
    console.log(row);
    setMyRow(row);
    setTitle("Promote to owner")
  };
  const handleClosePromoteParticpantDialog = () => {
    setOpenPromoteParticpantDialog(false);
  };

  function setParticipantsList(row){
    console.log(row);
    setParticipantsLista(tableData[row.index].participants);
    console.log(tableData[row.index].participants);
    handleModalOpen();
  }

  async function getData(){
    if (!tableData.length) {
      setIsLoading(true);
    } else {
      setIsRefetching(true);
    }

    try {
      const {data} = await apiToken.get('/list/backOffice/groups?elements='+pagination.pageSize+'&page='+pagination.pageIndex+'&pattern='+globalFilter ?? '');
      console.log(data);
      if(data.maxNumber===0){
        setTableData([]);
      }
      else{
        setTableData(JSON.parse(data.list));
        console.log(JSON.parse(data.list)[0]["participants"].length)
      }
      
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

  function closeDialog(){
    setCreateDialogOpen(false);
  }



  const handleDeleteRow = useCallback(
    (row) => {
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to delete the question: ${row.getValue('name')}`)
      ) {
        return;
      }
      
      deleteRow(row);
      
    },
    [deleteRow],
  ); 

  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function deleteRow(row){
    try{
      await apiToken.delete('/group?group='+row.id);
      tableData.splice(row.index, 1);
      setTableData([...tableData]);
      setRowCount(rowCount-1);
      SuccessAlert(vars.alerts.groups.success_delete);
    }catch(error){
      ErrorAlert(error.response.data);
    }
  }

  function createPublicGroup(){
    closeDialog();
    setOpenModal(true);
    setGroupType("público");
  }

  function createPrivateGroup(){
    closeDialog();
    setOpenModal(true);
    setGroupType("privado");

  }

  function ErrorAlert(text){
    setOpen({open:true,type:"error",text:text});
  }

  function SuccessAlert(text){
    setOpen({open:true,type:"success",text:text});
  }


  function closeModal(){
    setOpenModal(false);
  }

  async function submitModal(inputs){
    closeModal();
    try{
      await apiToken.post('/group',inputs);
      var data = {name:inputs.name,privacy:inputs.privacy,participants:1}
      tableData.push(data);
      setTableData([...tableData]);
      setRowCount(rowCount+1);
      SuccessAlert(vars.alerts.groups.success_create)
    }
    catch(error){
      ErrorAlert(error.response.data);
      console.log(error);
    }
  }

  //if you want to avoid useEffect, look at the React Query example instead
  useEffect(() => {
    getData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    globalFilter,
    pagination.pageIndex,
    pagination.pageSize,
  ]);

  const columns = useMemo(
    () => [
      {
        accessorKey: 'name',
        header: 'name',
      },
      {
        accessorKey: 'privacy',
        header: 'privacy',
      },
      {
        accessorKey: 'participants',
        header: 'Nº participants',
        Cell: ({ cell }) => (
          <span>{cell.getValue().length}</span>
        ),
      },
    ],
    [],
  );

  async function handleSubmitRemoveParticipant(delUser,name){
    try{
      const {data} = await apiToken.put('/group/remove/'+delUser+"?group="+name);
      console.log(data);
      handleCloseRemoveParticipantDialog();
      SuccessAlert("Participant removed with success!!")

    }catch(error){
      console.log(error);
      ErrorAlert(error.response.data);
    }
  }

  async function handleSubmitPromoteParticipant(delUser,name){
    try{
      const {data} = await apiToken.put('/group/assign/'+delUser+"?group="+name);
      SuccessAlert("New owner assign with success!!")
      handleClosePromoteParticpantDialog();
      console.log(data);
    }catch(error){
      console.log(error);
      ErrorAlert(error.response.data);
    }
  }

  return (
    <>
    <MaterialReactTable
      columns={columns}
      data={tableData}
      getRowId={(row) => row.name}
      manualPagination
      muiToolbarAlertBannerProps={
        isError
          ? {
              color: 'error',
              children: 'Error loading data',
            }
          : undefined
      }
      enableRowActions
      renderRowActions={({ row }) => (
        
        <Box sx={{ display: 'flex', flexWrap: 'nowrap', gap: '8px' }}>
          <Tooltip title="Participants list">
            <IconButton onClick={()=>{
              setParticipantsList(row);
            }}>
              <FormatListBulletedIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              color="error"
              onClick={() => handleDeleteRow(row)}
            >
              <DeleteIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title="Change Password">
            <IconButton
              onClick={() => handleOpenModalChangePwd(row.original)}
            >
              <PasswordIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Change Administrator">
            <IconButton
              color="primary"
              onClick={() => handleOpenPromoteParticpantDialog(row.original)}
            >
              <SupervisorAccountIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Remove person">
            <IconButton
              color="warning"
              onClick={() => handleOpenRemoveParticipantDialog(row.original)}
            >
              <PersonRemoveIcon/>
            </IconButton>
          </Tooltip>
        </Box>
        
      )}
      renderTopToolbarCustomActions={() => (
        <Stack direction="row" spacing={2} >
          <Button
            onClick={() => setCreateDialogOpen(true)}
            variant="contained"
            disabled={!validate.isUserAuthorized(vars.faqsAuth.create)}
          >
            Create New Group
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
    <Modal
      open={openParticipantsList}
      onClose={handleModalClose}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
    >
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2">
          Participantes List
        </Typography>
        {participantsList.map((elem)=>
          <>
            <span>{elem}</span><br/>
          </>
        )};
      </Box>
    </Modal>

    <AlertComponent
      openModal={open.open}
      type={open.type}
      text={open.text}
      onClose={handleClose}
    />

    <CreationGroupDialog
        open={CreateDialogOpen}
        onClose={closeDialog}
        createPublicGroup={createPublicGroup}
        createPrivateGroup={createPrivateGroup}
    />

    <GroupCreationModal
      open={openModal}
      onClose={closeModal}
      onSubmit={submitModal}
      type={groupType}
    />

    <ChangePassWordModal
        open={openModalChangePwd}
        ErrorAlert={ErrorAlert}
        SuccessAlert={SuccessAlert}
        onClose={handleCloseModalChangePwd}
        row={myRow}
    />

    <SelectElementDialog
      open={openRemoveParticipantDialog}
      onClose={handleCloseRemoveParticipantDialog}
      onSubmit={handleSubmitRemoveParticipant}
      ErrorAlert={ErrorAlert}
      SuccessAlert={SuccessAlert}
      row={myRow}
      title={title}
    />

    <SelectElementDialog
      open={openPromoteParticpantDialog}
      onClose={handleClosePromoteParticpantDialog}
      onSubmit={handleSubmitPromoteParticipant}
      ErrorAlert={ErrorAlert}
      SuccessAlert={SuccessAlert}
      row={myRow}
      title={title}
    />
    

    </>
  );



};



export default GroupsList;
