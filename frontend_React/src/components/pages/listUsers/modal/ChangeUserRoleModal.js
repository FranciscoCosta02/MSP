import React, {useState} from 'react';
import vars, { u } from '../../../../services/var/var';
import Cookies from "js-cookie";
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';

//Change user role Modal
export const ChangeUserRoleModal = ({ open, onClose, onSubmit,row,index }) => {

    const groups = JSON.parse(Cookies.get(vars.roles));
    const [newRole, setNewRole] = useState("");
  
    const handleSubmit = () => {
      onSubmit(index,row.username,newRole,row);
      setNewRole("");
      onClose();
    };
  
    const handleChange = (event) => {
      setNewRole(event.target.value);
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
  
              <p>Actual role:</p>
                <TextField
                  label="role"
                  name="role"
                  value={row.role}
                  disabled
                />
  
          <FormControl>
            <InputLabel id="demo-simple-select-label">New role:</InputLabel>
            <Select
              labelId="demo-simple-select-label"
              id="demo-simple-select"
              value={newRole}
              label="User type"
              onChange={handleChange}
            >
              {groups.map((elem)=>   
                  <MenuItem value={elem} disabled={elem===u.superUser}>{elem}</MenuItem>            
              )}
  
            </Select>
          </FormControl>
              
            </Stack>
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={onClose} variant="contained">
            Cancel
          </Button>
          <Button color="success" onClick={handleSubmit} variant="contained" disabled={row.role===newRole || row.role===""}>
            Update Role
          </Button>
        </DialogActions>
      </Dialog>
    );
  };