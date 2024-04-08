import React, {useState,useEffect} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';
import { TimeField } from '@mui/x-date-pickers/TimeField';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import CheckboxesGroup from './CheckboxesGroup';
import dayjs from 'dayjs';

//example of creating a mui dialog modal for creating new rows
export const EditRoomModal = ({ open, onClose,row,onSubmit,errorAlert }) => {
    const [valueHourStart, setValueHourStart] = useState(null);
    const [valueHourEnd, setValueHourEnd] = useState(null);
    const [stateWeekDays, setStateWeekDays] = useState({
      "Monday": false,
      "Tuesday": false,
      "Wednesday": false,
      "Thursday": false,
      "Friday": false,
      "Saturday": false,
    });

    useEffect(() => {
      setStateWeekDays({
        "Monday": row.data.weekDays.includes("Monday"),
        "Tuesday": row.data.weekDays.includes("Tuesday"),
        "Wednesday": row.data.weekDays.includes("Wednesday"),
        "Thursday": row.data.weekDays.includes("Thursday"),
        "Friday": row.data.weekDays.includes("Friday"),
        "Saturday": row.data.weekDays.includes("Saturday"),
      });
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [
      row
    ]);
    
  
    async function editRoom(){
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
       values.name=row.data.name;
       values.openTime=valueHourStart.format("HH:mm").toString();
       values.closeTime=valueHourEnd.format("HH:mm").toString();
       values.weekDays=newList.toString();
       values.department=row.data.department;
       values.availability=row.data.availability;
       console.log(values);
       onSubmit(row.index,values);
    }

    const handleClose = () => {
      onClose();      
    };
  
    const handleSubmit = () => {
      //put your validation logic here
      editRoom();
    };

    const handleChangeWeekDays = (event) => {
      setStateWeekDays({
        ...stateWeekDays,
        [event.target.name]: event.target.checked,
      });
    };
  
  
    return (
      <Dialog open={open}>
        <DialogTitle textAlign="center">Edit room</DialogTitle>
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
                    label="Room - Department"
                    name="name"
                    value={row.data.name+" - "+row.data.department || ""}
                    disabled
                  />
                  
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
          <Button color="success" onClick={handleSubmit} variant="contained" >
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    );
  };
