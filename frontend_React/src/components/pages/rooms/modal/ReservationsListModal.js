import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  IconButton,
  Tooltip,
} from '@mui/material';
import {apiToken} from '../../../../services/api/api'; 
import dayjs from 'dayjs';
import { DemoItem } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateField } from '@mui/x-date-pickers/DateField';
import SearchIcon from '@mui/icons-material/Search';
import ReservationListTable from './table/ReservationListTable';
//example of creating a mui dialog modal for creating new rows
export const ReservationsListModal = ({ open, row, onClose,errorAlert,successAlert }) => {
    const [day, setDay] = useState(dayjs());
    const [data, setData] = useState([]);
    const [isRefetching, setIsRefetching] = useState(false);


    async function getData(){
      console.log("getting reservations");
      setIsRefetching(true);
        try {
          const {data} = await apiToken.get('/rooms/'+row.department+"-"+row.name+"?date="+day.format("DD/MM/YYYY"));
          console.log(data);
          setData(data);
        } catch (error) {
          console.error(error);
          return;
        }
      setIsRefetching(false);
    }



    const handleClose = () => {
      onClose();
      setData([]);
    }

    function onDeleteReservation(index){
      data.splice(index, 1);
      setData([...data]);
    }

    return (
      <Dialog open={open} fullWidth>
        <DialogTitle textAlign="center">Booking list</DialogTitle>
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
                name="roomName"
                value={row.name+" - "+row.department}
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

              <ReservationListTable
                data={data}
                isRefetching={isRefetching}
                onDeleteReservation={onDeleteReservation}
              />


            </Stack>
            
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="primary" onClick={handleClose} variant="contained">
            Close
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


