import React,{useState,useEffect} from 'react';

import {
  Stack,
  TextField,
  Autocomplete,
  Button,
}from '@mui/material';
import {apiToken} from '../../../services/api/api';
import AlertComponent from '../../alerts/AlertComponent';
import vars from '../../../services/var/var';


export default function IndividualNotifications(props) {
    const [names, setNames] = useState([]);
    const [description, setDescription] = useState("");

    const [usersList, setUsersList] = useState([]);

    const [inputValue, setInputValue] = useState('');

    //Alert
    const [open, setOpen] = useState({open:false,type:"error",text:""});
    const handleClose = () => {
      setOpen({open:false,type:"error",text:""});
      
    };
    

    async function submit(e){
      e.preventDefault();
      var tmp = [];
      var i=0;
      for(i=0;i<names.length;i++){
        tmp.push(names[i].username);
      }
      console.log(tmp);
      console.log("Description: "+description);
      try{
        var inputs = {"dest":tmp,"text":description};
        console.log(inputs);
        const { data } = await apiToken.post('/feeds?type=user', inputs);
        console.log(data);
        setNames([]);
        setDescription("");
        setOpen({open:true,type:"success",text:vars.alerts.notifications.success});
      }
      catch(Error){
        console.log("Error: "+Error);
        setOpen({open:true,type:"error",text:vars.alerts.notifications.error});
      }
  }


  // eslint-disable-next-line react-hooks/exhaustive-deps
  async function getData(){
    try {
      const {data} = await apiToken.get('/list/backOffice/'+props.groupValue+'?elements=10&page=0&pattern='+inputValue ?? '');
      setUsersList(JSON.parse(data.list));
    } catch (error) {
      
      console.error("Error: "+error);
    }
  }

  

  useEffect(() => {

    getData();

      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, 
    [
      props.groupValue,
      inputValue,
      getData
    ]
    );

  return (
    <>
      <form onSubmit={submit}>
      <Stack direction="row" spacing={2} >
        
        <Autocomplete
          multiple
          limitTags={2}
          id="multiple-limit-tags"
          options={usersList}
          value={names}
          onChange={(event, newValue) => {
            console.log(newValue);
            setNames([
              ...newValue,
            ]);
          }}
          inputValue={inputValue}
          onInputChange={(event, newInputValue) => {
            console.log("Searching for: "+newInputValue);
            setInputValue(newInputValue);
          }}
          getOptionLabel={(option) => option.username+"  ("+option.name+")"}
          renderInput={(params) => (
            <TextField {...params} label="Users" placeholder="Users" />
          )}
          sx={{ width: '500px' }}
        />
        </Stack>

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
