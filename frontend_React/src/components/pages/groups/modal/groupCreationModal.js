import React,{useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';


function GroupCreationModal(props) {
  const { onClose,onSubmit, open,type } = props;
  const [inputs, setInputs] = useState({name:"",confirmation:"",password:"",privacy:type});

  function cleanInputs(){
    setInputs({name:"",confirmation:"",password:"",privacy:type});
  }

  const handleClose = () => {
    onClose();
    cleanInputs();
  }

  const handleSubmit = () => {
    if(type==="privado"){
      inputs.privacy="Private";
    }
    else{
      inputs.privacy="Public";
    }
    onSubmit(inputs);
    cleanInputs();
  };

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setInputs(values => ({...values, [name]: value}))
  }

  return (
    <Dialog open={open}>
      <DialogTitle textAlign="center">Criação de grupo {type}</DialogTitle>
      <DialogContent>
        <form onSubmit={(e) => e.preventDefault()}>
          <Stack
            sx={{
              width: '100%',
              minWidth: { xs: '300px', sm: '360px', md: '400px' },
              gap: '1.5rem',
            }}
          >
              <br/>
              <TextField
                key="name"
                label="Name"
                name="name"
                value={inputs.name}
                onChange={handleChange}
              />
            {
                type==="privado" &&
                <>
                    <TextField
                        key="password"
                        label="Password"
                        name="password"
                        value={inputs.password}
                        onChange={handleChange}
                    />

                    <TextField
                        key="confirmation"
                        label="Confirmation"
                        name="confirmation"
                        value={inputs.confirmation}
                        onChange={handleChange}
                    />
                </>
            }
            
          </Stack>
        </form>
      </DialogContent>
      <DialogActions sx={{ p: '1.25rem' }}>
        <Button color="error" onClick={handleClose} variant="contained">
          Cancel
        </Button>
        <Button color="success" onClick={handleSubmit} variant="contained" disabled={(type==="privado" && (inputs.password!==inputs.confirmation || inputs.confirmation==="")) || inputs.name===""}>
          Create group
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default GroupCreationModal;

