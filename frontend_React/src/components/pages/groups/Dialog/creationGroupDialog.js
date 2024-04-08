import * as React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';


function CreationGroupDialog(props) {
  const { onClose, open,createPublicGroup,createPrivateGroup } = props;

  const handleClose = () => {
    onClose();
  };

  return (
    <Dialog onClose={handleClose} open={open}>
      <DialogTitle>Tipo de grupo:</DialogTitle>
      <List sx={{ pt: 0 }}>
        
          <ListItem disableGutters>
            <ListItemButton onClick={createPublicGroup}>
                <ListItemText primary="Grupo público" />
            </ListItemButton>
          </ListItem>

          <ListItem disableGutters>
            <ListItemButton onClick={createPrivateGroup}>
              <ListItemText primary="Grupo privado" />
            </ListItemButton>
          </ListItem>

      </List>
    </Dialog>
  );
}

export default CreationGroupDialog;

