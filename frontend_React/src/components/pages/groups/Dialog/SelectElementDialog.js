import React,{useState,useEffect} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  MenuItem,
  Select,
  InputLabel,
  DialogContentText,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';


function SelectElementDialog(props) {
  const { onClose,onSubmit, open,row,title,ErrorAlert,SuccessAlert } = props;
  const [name, setName] = useState("");
  const [namesList, setParticipantsList] = useState(["Please, wait!"]);
  const theme = useTheme();

  useEffect(() => {
    console.log(row.participants);
    setParticipantsList(row.participants || ["Please, wait!"]);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    row.name
  ]);

  
  const handleClose = () => {
    onClose();
  }

  const handleSubmit = () => {
    console.log(name);
    onSubmit(name,row.name);
  };

  const handleChange = (event) => {
    setName(event.target.value);
  };

  function getStyles(name, personName, theme) {
    return {
      fontWeight:
        personName.indexOf(name) === -1
          ? theme.typography.fontWeightRegular
          : theme.typography.fontWeightMedium,
    };
  }


  return (
    <Dialog open={open}>
      <DialogTitle textAlign="center">{title}</DialogTitle>
    
      <DialogContent>
      <DialogContentText>Owner: {row.owner}</DialogContentText>
        <form onSubmit={(e) => e.preventDefault()}>
        <FormControl fullWidth style={{marginTop:"15px"}}>
          <InputLabel id="demo-simple-select-label">Participants</InputLabel>
          <Select
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={name}
            label="Participants"
            onChange={handleChange}
          >
            {namesList.map((name) => (
            <MenuItem
              value={name}
              style={getStyles(name, name, theme)}
            >
              {name}
            </MenuItem>
          ))}
          </Select>
        </FormControl>
        </form>
      </DialogContent>
      <DialogActions sx={{ p: '1.25rem' }}>
        <Button color="error" onClick={handleClose} variant="contained">
          Cancel
        </Button>
        <Button color="success" onClick={handleSubmit} variant="contained" disabled={false}>
          Submit
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default SelectElementDialog;

