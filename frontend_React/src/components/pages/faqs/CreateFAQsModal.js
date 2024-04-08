import React, {useState} from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  FormControl,
  MenuItem,
  InputLabel,
  OutlinedInput,
  useTheme,
  Select
} from '@mui/material';
import {apiToken} from '../../../services/api/api';
import vars from '../../../services/var/var';

function getStyles(name, personName, theme) {
  return {
    fontWeight:
      personName.indexOf(name) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}


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

const names = vars.mock.faqTagList;

//example of creating a mui dialog modal for creating new rows
export const CreateFAQsModal = ({ open, columns, onClose, onSubmit,errorAlert }) => {
  const theme = useTheme();
    const [values, setValues] = useState(() =>
      columns.reduce((acc, column) => {
        acc[column.accessorKey ?? ''] = '';
        return acc;
      }, {}),
    );
  
    const [tag, setTag] = useState([]);
    async function createFAQ(values){
      try{
        console.log("Create FAQ");
        console.log(values);
        const {data} = await apiToken.post('/faq',values);
        onSubmit(values);
        console.log(data);
      }catch(error){
        errorAlert(error)
      }
    }
  
    const handleSubmit = () => {
      //put your validation logic here
      console.log(values);
      values.tag=tag[0];
      createFAQ(values);
      onClose();
    };

    


    const handleChange = (event) => {
      const {
        target: { value },
      } = event;
      setTag(
        // On autofill we get a stringified value.
        typeof value === 'string' ? value.split(',') : value,
      );
    };

    return (
      <Dialog open={open}>
        <DialogTitle textAlign="center">Create New Question</DialogTitle>
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
                { column.accessorKey!=="tag" && 
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
              <FormControl >
                <InputLabel id="tag">Tag</InputLabel>
                <Select
                  labelId="tag"
                  id="tag"
                  name="tag"
                  value={tag}
                  onChange={handleChange}
                  input={<OutlinedInput label="Name" />}
                  MenuProps={MenuProps}
                >
                  {names.map((name) => (
                    <MenuItem
                      key={name}
                      value={name}
                      style={getStyles(name, tag, theme)}
                    >
                      {name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Stack>
          </form>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={onClose} variant="contained">
            Cancel
          </Button>
          <Button color="success" onClick={handleSubmit} variant="contained">
            Create New Question
          </Button>
        </DialogActions>
      </Dialog>
    );
  };
