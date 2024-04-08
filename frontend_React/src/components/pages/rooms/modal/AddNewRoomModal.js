import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  Select,
  FormControl,
  MenuItem,
  InputLabel
} from '@mui/material';
import {apiToken} from '../../../../services/api/api';
import { TimeField } from '@mui/x-date-pickers/TimeField';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import vars from '../../../../services/var/var';
import CheckboxesGroup from './CheckboxesGroup';
import { useTheme } from '@mui/material/styles';
import OutlinedInput from '@mui/material/OutlinedInput';
import dayjs from 'dayjs';


const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};


function getStyles(name, personName, theme) {
  return {
    fontWeight:
      personName.indexOf(name) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}



//example of creating a mui dialog modal for creating new rows
export const AddNewRoomModal = ({ open, onClose, onSubmit,errorAlert }) => {
    
    const theme = useTheme();
    const [department, setDepartment] = useState("");
    const [valueHourStart, setValueHourStart] = useState(null);
    const [valueHourEnd, setValueHourEnd] = useState(null);
    const [name,setName] = useState("");

    const [stateWeekDays, setStateWeekDays] = useState({
      "Monday": true,
      "Tuesday": true,
      "Wednesday": true,
      "Thursday": true,
      "Friday": true,
      "Saturday": false,
    });
  
    async function createRoom(){
      var values = {};
      var newList = [];
      Object.keys(stateWeekDays).forEach(function(item){
        if(stateWeekDays[item]){
          newList.push(item);
        }
       });
      if(valueHourStart===null || valueHourEnd==null){
        errorAlert("openTime == null or endTime == null");
        return;
      }
      else if(valueHourStart.isAfter(valueHourEnd)){
        errorAlert("openTime > endTime");
        return;
      }
      else if(dayjs(valueHourStart).isSame(valueHourEnd)){
        errorAlert("openTime = endTime");
        return;
      }
       console.log(newList);
       console.log(newList.toString());
       values.name=name;
       values.openTime=valueHourStart.format("HH:mm").toString();
       values.closeTime=valueHourEnd.format("HH:mm").toString();
       values.weekDays=newList.toString();
       values.department=department;
       console.log(values);
      try{
        console.log(values);
        const {data} = await apiToken.post('/rooms',values);
        onSubmit(values);
        console.log(data);
        handleClose();
      }catch(error){
        errorAlert(error.response.data);
      }
    }

    const handleClose = () => {
      onClose();
      setDepartment("");
      
    };
  
    const handleSubmit = () => {
      //put your validation logic here
      createRoom();
      
    };

    const handleChangeWeekDays = (event) => {
      setStateWeekDays({
        ...stateWeekDays,
        [event.target.name]: event.target.checked,
      });
    };
  
    const handleChangeDeparment = (event) => {
  
      setDepartment(event.target.value);
    };
   
    
  
    return (
      <Dialog open={open}>
        <DialogTitle textAlign="center">Add new room</DialogTitle>
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
                    label="Room name"
                    name="name"
                    onChange={(e) => setName(e.target.value)}
                  />
                  
                  <FormControl fullWidth>
                    <InputLabel id="demo-multiple-name-label">Department</InputLabel>
                    <Select
                      labelId="demo-multiple-name-label"
                      id="demo-multiple-name"
                    
                      value={department}
                      onChange={handleChangeDeparment}
                      input={<OutlinedInput label="Name" />}
                      MenuProps={MenuProps}
                    >
                      {vars.mock.departments.map((name) => (
                        <MenuItem
                          key={name}
                          value={name}
                          style={getStyles(name, department, theme)}
                        >
                          {name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <CheckboxesGroup state={stateWeekDays} handleChange={handleChangeWeekDays}/>

                
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <TimeField
                      label="openTime"
                      name="openTime"
                      onChange={(newValue) => setValueHourStart(newValue)}
                      format="HH:mm"
                    />
                  </LocalizationProvider>

                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <TimeField
                      label="closeTime"
                      name="closeTime"
                      onChange={(newValue) => setValueHourEnd(newValue)}
                      format="HH:mm"
                    />
                  </LocalizationProvider>
                
     

            </Stack>
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={handleClose} variant="contained">
            Cancel
          </Button>
          <Button color="success" onClick={handleSubmit} variant="contained" disabled={name.trim()==="" || department.trim()===""}>
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    );
  };
