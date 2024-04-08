import React from 'react';
import Box from '@mui/material/Box';
import FormControl from '@mui/material/FormControl';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid';
import { styled } from '@mui/material/styles';

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'left',
  color: theme.palette.text.secondary,
}));

export default function CheckboxesGroup(props) {
  const { state,handleChange } = props;

  

  return (
    <Box sx={{ display: 'flex' }}>
      <FormControl sx={{ m: 3 }} component="fieldset" variant="standard">
        <FormGroup>
        <Grid container spacing={2}>
          <Grid item xs={6} md={6}>
            <Item>
            <FormControlLabel
              control={
                <Checkbox checked={state["Monday"]} onChange={handleChange} name="Monday" />
              }
              label="Monday"
            />
            <br/>
            <FormControlLabel
              control={
                <Checkbox checked={state["Wednesday"]} onChange={handleChange} name="Wednesday" />
              }
              label="Wednesday"
            />
            <br/>
            <FormControlLabel
              control={
                <Checkbox checked={state["Friday"]} onChange={handleChange} name="Friday" />
              }
              label="Friday"
            />
            </Item>
            
          </Grid>
          <Grid item xs={6} md={6}>
            <Item>
            <FormControlLabel
              control={
                <Checkbox checked={state["Tuesday"]} onChange={handleChange} name="Tuesday" />
              }
              label="Tuesday"
            />
            <br/>
            <FormControlLabel
              control={
                <Checkbox checked={state["Thursday"]} onChange={handleChange} name="Thursday" />
              }
              label="Thursday"
            />
            <br/>
            <FormControlLabel
              control={
                <Checkbox checked={state["Saturday"]} onChange={handleChange} name="Saturday" />
              }
              label="Saturday"
            />
            </Item>
          </Grid>
        </Grid>
        </FormGroup>
      </FormControl>
    </Box>
  );
}