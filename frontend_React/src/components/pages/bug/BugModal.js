import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';
import { apiToken } from '../../../services/api/api';
import AlertComponent from '../../alerts/AlertComponent';
import vars from '../../../services/var/var';


//example of creating a mui dialog modal for creating new rows
export const BugModal = ({ open, onClose,onSubmit }) => {

    //Alert
    const [openAlert, setOpenAlert] = useState({open:false,type:"success",text:""});
    const handleCloseAlert = () => {
      setOpenAlert({open:false,type:"success",text:""});
    };
    
    const [reason, setReason] = useState("");
    const [text, setText] = useState("");
    
  
    async function reportBug(){
      try{
        var tmp = {reason:reason,text:text}
        const {data} = await apiToken.post('/bug',tmp);
        console.log(data);
        onClose();
        setOpenAlert({open:true,type:"success",text:vars.alerts.bug.success});
      }
      catch(error){
        console.log(error);
        setOpenAlert({open:true,type:"error",text:vars.alerts.bug.error});
      }
    }

    const handleClose = () => {
      onClose();      
    };
  
    const handleSubmit = () => {
      //put your validation logic here
      reportBug();
    };


  
    return (
      <>
      <Dialog open={open}>
        <DialogTitle textAlign="center">Report Bug</DialogTitle>
        <br/>
        <DialogContent>
          <form onSubmit={(e) => e.preventDefault()}>
            <Stack
              sx={{
                width: '100%',
                minWidth: { xs: '300px', sm: '360px', md: '400px' },
                gap: '1.5rem',
              }}
            >
                  
                  <TextField
                    label="Reason"
                    name="reason"
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                  />

                  <TextField
                    label="Description"
                    name="text"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                  />
                  
                  
            </Stack>
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={handleClose} variant="contained">
            Cancel
          </Button>
          <Button color="primary" onClick={handleSubmit} variant="contained" >
            Report
          </Button>
        </DialogActions>
      </Dialog>
      <AlertComponent
        openModal={openAlert.open}
        type={openAlert.type}
        text={openAlert.text}
        onClose={handleCloseAlert}
      />
      </>
    );
  };
