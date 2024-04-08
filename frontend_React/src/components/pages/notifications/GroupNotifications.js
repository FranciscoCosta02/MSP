import React,{useState} from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import {apiToken} from '../../../services/api/api';
import AlertComponent from '../../alerts/AlertComponent';
import vars from '../../../services/var/var';



export default function GroupNotifications(props) {
    const [description, setDescription] = useState("");
    

    //Alert
    const [open, setOpen] = useState({open:false,type:"error",text:""});
    const handleClose = () => {
      setOpen({open:false,type:"error",text:""});
      
    };



    async function submit(e){
        e.preventDefault();
        console.log("array: ");
        console.log(props.groupName);
        console.log("Description: "+description);
        if(description===""){
          setOpen({open:true,type:"error",text:vars.alerts.notifications.empty});
          return;
        }
        try{
          var inputs = {"dest":props.groupName,"text":description};
          console.log(inputs);
          const { data } = await apiToken.post('/feeds?type=role', inputs);
          console.log(data);
          setDescription("");
          setOpen({open:true,type:"success",text:vars.alerts.notifications.success});
        }
        catch(Error){
          console.log("Error: "+Error);
          setOpen({open:true,type:"error",text:vars.alerts.notifications.error});
        }
    }

  return (
    <>
        
      <form onSubmit={submit}>
        
        <TextField
          id="outlined-multiline-static"
          label="Text"
          multiline
          margin="dense"
          value={description}
          onChange={(e)=>setDescription(e.target.value)}
          rows={8}
          fullWidth
        />
        <Button variant="contained" type="submit">Send Notification</Button>
      
      </form>
      <AlertComponent
            openModal={open.open}
            type={open.type}
            text={open.text}
            onClose={handleClose}
          />
    </>
  );
}