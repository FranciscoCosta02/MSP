import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';

//Delete user Modal
export const DeleteUserModal = ({ open, onClose, onSubmit,row,username }) => {

    const [confirmation, setConfirmation] = useState("");
  
  
    const handleSubmit = () => {
  
      if(username!==row.id){
        alert("Os nomes de utilizador não coincidem");
      }
      else{
        onSubmit(row.index,username);
        setConfirmation("");
      }
      onClose();
    };
  
    const handleChange = (event) => {
      setConfirmation(event.target.value);
    };
  
    return (
      <Dialog open={open}>
        <DialogTitle textAlign="center">Update Role</DialogTitle>
        <DialogContent>
          <form onSubmit={(e) => e.preventDefault()}>
            <Stack
              sx={{
                width: '100%',
                minWidth: { xs: '300px', sm: '360px', md: '400px' },
                gap: '1.5rem',
              }}
            >
  
              <p>confirme o nome do username: ({username})</p>
                <TextField
                  label="Username"
                  name="Username"
                  value={confirmation}
                  onChange={handleChange}
                />
  
            </Stack>
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={onClose} variant="contained">
            Cancel
          </Button>
          <Button color="success" onClick={handleSubmit} variant="contained" disabled={confirmation!==username || confirmation===""}>
            Delete User
          </Button>
        </DialogActions>
      </Dialog>
    );
  };
  