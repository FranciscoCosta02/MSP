import React, {useState} from 'react';
import {apiToken} from '../../../../services/api/api';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';


//example of creating a mui dialog modal for creating new rows
export const CreateAnomalyModal = ({ open, columns, onClose, onSubmit }) => {
  const [values, setValues] = useState(() =>
    columns.reduce((acc, column) => {
      acc[column.accessorKey ?? ''] = '';
      return acc;
    }, {}),
  );

  async function createAnomaly(values){
    try{
      const {data} = await apiToken.post('/anomaly',{"reason":values.reason,"text":values.text});
      onSubmit(values);
      console.log(data);
    }catch(error){
      console.log(error);
    }
  }

  const handleSubmit = () => {
    //put your validation logic here
    console.log(values);
    createAnomaly(values);
    onClose();
  };

  return (
    <Dialog open={open}>
      <DialogTitle textAlign="center">Create new anomaly</DialogTitle>
      <DialogContent>
        <form onSubmit={(e) => e.preventDefault()}>
          <Stack
            sx={{
              width: '100%',
              minWidth: { xs: '300px', sm: '360px', md: '400px' },
              gap: '1.5rem',
            }}
          >
            {columns.map((column) => (
              <>
              {
                ["reason","text"].includes(column.accessorKey) &&
                <TextField
                key={column.accessorKey}
                label={column.header}
                name={column.accessorKey}
                onChange={(e) =>
                  setValues({ ...values, [e.target.name]: e.target.value })
                }
              />
            }
              </>
            ))}
          </Stack>
        </form>
      </DialogContent>
      <DialogActions sx={{ p: '1.25rem' }}>
        <Button color="error" onClick={onClose} variant="contained">
          Cancel
        </Button>
        <Button color="success" onClick={handleSubmit} variant="contained">
          Create new anomaly
        </Button>
      </DialogActions>
    </Dialog>
  );
};

