import React, {useState,useEffect} from 'react';
import {apiToken} from '../../../services/api/api';
//import vars from '../../../services/var/var';
import dayjs from 'dayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { MobileDateTimePicker } from '@mui/x-date-pickers/MobileDateTimePicker';
import AlertComponent from '../../alerts/AlertComponent';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';

const EditActivity = (props) => {
  const { onClose,onSubmit,errorAlert,open,row,index} = props;
  const [startDate, setStartDate] = useState(dayjs());
  const [data, setData] = useState({title:"",description:"",maxParticipants:""});
  const [endDate, setEndDate] = useState(dayjs());


  const handleClose = () => {
    onClose();
    cleanTextFields();
  };

  useEffect(() => {
    setStartDate(dayjs(row.startDate));
    setEndDate(dayjs(row.endDate));
    var tmp  = {}
    tmp.title=row.title;
    tmp.username=row.username;
    tmp.description=row.description;
    tmp.maxParticipants=row.maxParticipants;
    tmp.startDate=row.startDate;
    tmp.endDate=row.endDate;
    tmp.id=row.id;
    setData(tmp);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    row
  ]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setData(values => ({...values, [name]: value}))
  }

  function cleanTextFields(){
    setStartDate(dayjs());
    setEndDate(dayjs());
  }

  async function submit(){
    var tmp = data;
    tmp.endDate=endDate.format().toString();
    tmp.startDate=startDate.format().toString();
    console.log("TMP");
    console.log(tmp);   
    try{
      const {data} = await apiToken.put('/activity/update',tmp);
      console.log(data);
      onSubmit(index,tmp);
    }
    catch(error){
      errorAlert(error.response.data);
    }
     
  }
  

  return (
    <>
     <Dialog open={open}>
      <DialogTitle textAlign="center">Editar Atividade</DialogTitle>
      <DialogContent>
      <div>
      <TextField fullWidth label="Title" margin="dense" id="fullWidth" name="title" value={data.title} onChange={handleChange}/>
      <TextField
          id="description"
          label="Description"
          multiline
          name="description"
          margin="dense"
          value={data.description}
          onChange={handleChange}
          rows={5}
          fullWidth
        />
      <TextField fullWidth label="Max. Participants" margin="dense" id="fullWidth" name="maxParticipants" value={data.maxParticipants} onChange={handleChange}/>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DemoContainer components={['DateTimePicker']}>
          <MobileDateTimePicker label="Start Date" name="startDate" value={startDate}
          onChange={(newValue) => setStartDate(newValue)}
          />
        </DemoContainer>
        <DemoContainer components={['DateTimePicker']} >
          <MobileDateTimePicker label="End Date" name="endDate" value={endDate}
          onChange={(newValue) => setEndDate(newValue)}
          
          />
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
        <Button color="success" onClick={submit} variant="contained">
          Edit activity
        </Button>
      </DialogActions>
    </Dialog>
    </>
    
  );
};

export default EditActivity;
//<Button variant="contained" style={{"margin-top":"5px"}} onClick={submit}>Submit</Button>