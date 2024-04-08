import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  FormControl,
  MenuItem,
  OutlinedInput,
  Checkbox,
  ListItemText,
  InputLabel,
  Select,
  FormControlLabel,
  IconButton,
  Tooltip,
} from '@mui/material';
import {apiToken} from './../../../../services/api/api'; 
import vars from '../../../../services/var/var';
import dayjs from 'dayjs';
import { DemoItem } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateField } from '@mui/x-date-pickers/DateField';
import SearchIcon from '@mui/icons-material/Search';
//example of creating a mui dialog modal for creating new rows
export const MakeAnAppointmentModal = ({ open, row, onClose,errorAlert,successAlert }) => {
    const [userName, setUsername] = useState("");
    const [hour, setHour] = useState([]);
    const [hours, setHours] = useState([]);
    const [day, setDay] = useState(dayjs());
    const [checked, setChecked] = useState(true);
    const reserved = " - reservado por: ";


    function getWeekDay(day){
      var weekDays = vars.mock.weekDay;
      switch(day){
        case 1:return weekDays[0];
        case 2:return weekDays[1];
        case 3:return weekDays[2];
        case 4:return weekDays[3];
        case 5:return weekDays[4];
        case 6:return weekDays[5];
        default:return "Sunday";
      }
    }

    async function MakeAnAppointment(){
      var finalHours = [];
      for(var i=0;i<hours.length;i++){
        var tmp = hours[i].split(reserved);
        finalHours.push(tmp[0]);
      }
      console.log(day.add(1,"day").toISOString());
      var json = {roomName:row.name,roomDepartment:row.department
        ,time:finalHours.toString(),date:day.format("DD/MM/YYYY")
        ,username:(checked)?localStorage.getItem(vars.username):userName
        ,weekDay:getWeekDay(day.day()),fullTime:day.add(1,"day").toISOString()}
      console.log(json);
      try{
        await apiToken.post('/rooms/reserve',json);
        successAlert();
        onClose();
        cleanAll();
      }catch(error){
        errorAlert(error.response.data);
      }
    }



    async function getData(){
      console.log("getting non-available hours");
      setHours([]);
      setHour([]);
        try {
          const {data} = await apiToken.get('/rooms/'+row.department+"-"+row.name+"?date="+day.format("DD/MM/YYYY"));
          console.log("DONE!")
          setAppointments(data);
        } catch (error) {
          console.error(error);
          return;
        }
    }

    function setAppointments(data){
      var time_list = [];
      var username_list = [];
      console.log(data);
      for(var i=0;i<data.length;i++){
        time_list.push(data[i].time)
        username_list.push(data[i].username);
      }
      var begin = row.openTime;
      var close = row.closeTime;
      var begin_h = parseInt(begin.split(":")[0]);//parseInt(begin[0]+begin[1],10);
      var begin_m = parseInt(begin.split(":")[1]);
      var close_h = parseInt(close.split(":")[0]);
      var close_m = parseInt(close.split(":")[1]);
      begin=0;
      close=0;
      if(begin_m===0){
        begin=0+begin_h;
      }
      else{
        begin=0+begin_h+1;
      }
      if(close_m===0){
        close=0+close_h;
      }
      else{
        close=0+close_h;
      }
      var hours_list=[];
      do{
        var user = getUsername(time_list,username_list,begin);
        if(begin<=9)
          hours_list.push("0"+begin+":00"+user);
        else
          hours_list.push(begin+":00"+user);
        begin++;
      }while(begin<close);

      setHour(hours_list);

    }

    function getUsername(time_list,username_list,begin){
      for(var i=0;i<time_list.length;i++){
        if(begin===parseInt(time_list[i].split(":")[0])){
          return reserved+username_list[i];
        }
      }
      return "";
    }


    function cleanAll(){
      setUsername("");
      setChecked(true);
      setHours([]);
      setHour([]);
    }

    const handleClose = () => {
      onClose();
      cleanAll();
    }
  
    const handleSubmit = () => {
      //put your validation logic here
      if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to proceed?`)
      ) {
        return;
      }
      MakeAnAppointment();
      
    };

    const MenuProps = {
      PaperProps: {
        style: {
          maxHeight: 48 * 4.5 + 8,
          width: 250,
        },
      },
    };

    const handleChange = (event) => {
      const {
        target: { value },
      } = event;
      setHours(
        // On autofill we get a stringified value.
        typeof value === 'string' ? value.split(',') : value,
      );
    };
    

    const handleCheckChange = (event) => {
      setChecked(event.target.checked);
    };


    return (
      <Dialog open={open}>
        <DialogTitle textAlign="center">Book room</DialogTitle>
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
                label="Room"
                name="roomName"
                value={row.name}
                disabled
              />

              <TextField
                label="Department"
                name="roomDepartment"
                value={row.department}
                disabled
              />
              <Stack direction="row" spacing={2}>
              <LocalizationProvider dateAdapter={AdapterDayjs} fullWidth>
                <DemoItem label="Date">
                  <DateField value={day} onChange={(newValue) => setDay(newValue)} format="DD/MM/YYYY"/>
                </DemoItem>
              </LocalizationProvider>
              <Tooltip title="Search for available hours">
                <IconButton
                  color="primary"
                  onClick={() => {
                    getData()
                  }}
                  style={{marginTop:20}}
                >
                  <SearchIcon />
                </IconButton>
              </Tooltip>
              </Stack>
              <FormControl>
                <InputLabel id="demo-multiple-checkbox-label">Availabel hours</InputLabel>
                <Select
                  labelId="demo-multiple-checkbox-label"
                  id="demo-multiple-checkbox"
                  multiple
                  value={hours}
                  onChange={handleChange}
                  input={<OutlinedInput label="Hours" />}
                  renderValue={(selected) => selected.join(', ')}
                  MenuProps={MenuProps}
                  disabled={hour===[]}
                >
                  {hour.map((name) => (
                    <MenuItem key={name} value={name}>
                      <Checkbox checked={hours.indexOf(name) > -1} />
                      <ListItemText primary={name} />
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

            </Stack>
            <Stack>
            <FormControlLabel
              label="my own appointment"
              control={
                <Checkbox
                    checked={checked}
                    onChange={handleCheckChange}
                    inputProps={{ 'aria-label': 'controlled' }}
                  />
              }/>
              {
                !checked &&
                <TextField
                  label="Username"
                  name="username"
                  value={userName}
                  onChange={(event) => {
                    setUsername(event.target.value);
                  }}
                />
              }
              </Stack>
            
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={handleClose} variant="contained">
            Cancel
          </Button>
          <Button color="success" onClick={handleSubmit} variant="contained" disabled={hours.length===0}>
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    );
  };



/*
  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    
  }
*/


