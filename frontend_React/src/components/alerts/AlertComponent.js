import React from 'react';
import Alert from '@mui/material/Alert';
import Snackbar from '@mui/material/Snackbar';
import vars from '../../services/var/var';

//example of creating a mui dialog modal for creating new rows
const AlertComponent = ({ openModal, type,text,onClose }) => {

  const handleClose = () => {
    onClose();
  };

  return (
    <>
      <Snackbar open={openModal} autoHideDuration={vars.alerts.alertAutoHideDuration} onClose={handleClose}>
        <Alert onClose={handleClose} severity={type} sx={{ width: '100%' }}>
          {text}
        </Alert>
      </Snackbar>
    </>
  );
};

export default AlertComponent;


