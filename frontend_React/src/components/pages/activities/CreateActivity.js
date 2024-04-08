import React, {useState} from 'react';
import {apiToken} from '../../../services/api/api';
//import vars from '../../../services/var/var';
import dayjs from 'dayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { MobileDateTimePicker } from '@mui/x-date-pickers/MobileDateTimePicker';
import vars from '../../../services/var/var';
import AlertComponent from '../../alerts/AlertComponent';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';

const CreateActivity = (props) => {
  const { onClose,onSubmit,errorAlert,open} = props;
  const [startDate, setStartDate] = useState(dayjs());
  const [endDate, setEndDate] = useState(dayjs());
  const [data, setData] = useState({title:"",description:"",maxParticipants:""});


  const handleClose = () => {
    onClose();
    cleanTextFields();
  };

  function cleanTextFields(){
    setData({title:"",description:"",maxParticipants:""});
    setStartDate(dayjs());
    setEndDate(dayjs());
  }

  async function submit(){
      //var tmp = {title:title,description:description,maxParticipants:maxParticipants,startDate:startDate.format().toString(),endDate:endDate.format().toString(),username:localStorage.getItem(vars.username)}      
      var tmp = data;
      tmp.endDate=endDate.format().toString();
      tmp.startDate=startDate.format().toString();
      tmp.username=localStorage.getItem(vars.username);
      console.log(tmp);   
      try{
        const {data} = await apiToken.post('/activity',tmp);
        console.log(data);
        onSubmit(tmp);
        onClose();
        cleanTextFields();
      }
      catch(error){
        errorAlert(error.response.data)
        console.log(error);
      }
  }

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setData(values => ({...values, [name]: value}))
  }
  

  return (
    <>
     <Dialog open={open}>
      <DialogTitle textAlign="center">Criação de Atividade</DialogTitle>
      <DialogContent>
      <div>
      <TextField fullWidth label="Title" margin="dense" id="fullWidth" value={data.title} name="title" onChange={handleChange}/>
      <TextField
          id="outlined-multiline-static"
          label="Description"
          multiline
          margin="dense"
          value={data.description}
          name="description"
          onChange={handleChange}
          rows={5}
          fullWidth
        />
      <TextField fullWidth label="Max. Participants" margin="dense" id="fullWidth" name="maxParticipants" value={data.maxParticipants} onChange={handleChange}/>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DemoContainer components={['DateTimePicker']}>
          <MobileDateTimePicker label="Start Date" value={startDate}
          onChange={(newValue) => setStartDate(newValue)}/>
        </DemoContainer>
        <DemoContainer components={['DateTimePicker']} >
          <MobileDateTimePicker label="End Date" value={endDate}
          onChange={(newValue) => setEndDate(newValue)}/>
        </DemoContainer>
      </LocalizationProvider>
      
      </div>
      <AlertComponent
        openModal={open.open}
        type={open.type}
        text={open.text}
        onClose={handleClose}
      />
      </DialogContent>
      <DialogActions sx={{ p: '1.25rem' }}>
        <Button color="error" onClick={handleClose} variant="contained">
          Cancel
        </Button>
        <Button color="success" onClick={submit} variant="contained" disabled={startDate.isAfter(endDate) || data.title==="" || data.description===""}>
          Create activity
        </Button>
      </DialogActions>
    </Dialog>
    </>
    
  );
};

export default CreateActivity;
//<Button variant="contained" style={{"margin-top":"5px"}} onClick={submit}>Submit</Button>