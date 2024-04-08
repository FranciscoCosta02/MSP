import React,{useState} from "react";
import './../login/AlertStyle.css';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  IconButton,
  OutlinedInput,
  InputLabel,
  InputAdornment,
  FormControl,
} from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';

const SetNewPassword = (props) => {
  const {open,onClose,onSubmit} = props
  
  const[code,setCode]=useState("");
  const [inputs, setInputs] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setInputs(values => ({...values, [name]: value}))
  }

  const handleClickShowPassword = () => setShowPassword((show) => !show);

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  function handleSubmit(){
    onSubmit(inputs,code);
    cleanALL();
  }

  const handleClickShowConfirmation = () => setShowConfirmation((show) => !show);

  const handleMouseDownConfirmation = (event) => {
    event.preventDefault();
  };

  function cleanALL(){
    setCode("")
    setInputs({});
    setShowPassword(false);
    setShowConfirmation(false);
  }

  const handleClose = () => {
    onClose();
    cleanALL();
  };

  const divStyle = {
    marginBottom: "10px",
  };

    return(
      <>
      <Dialog open={open}>
        <DialogTitle textAlign="center">Recover Password</DialogTitle>
        <DialogContent>
        <div className="text-center hero my-5">
              <TextField className="loginFormTextF" id="outlined-basic" label="Code" value={code} onChange={(e)=>setCode(e.target.value)} variant="outlined"  style={divStyle}/>
              <FormControl className="loginFormTextF" variant="outlined" style={divStyle}>
                <InputLabel >Password</InputLabel>
                <OutlinedInput
                  id="outlined-basic"
                  label="Password"
                  type={showPassword ? 'text' : 'password'}
                  variant="outlined"
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPassword}
                        onMouseDown={handleMouseDownPassword}
                        edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  }
                  value={inputs.newPwd || ""}
                  onChange={handleChange}
                  name="newPwd"
                />       
              </FormControl>
              <FormControl className="loginFormTextF" variant="outlined"  style={divStyle}>
                <InputLabel >Confirmation</InputLabel>
                <OutlinedInput
                  id="outlined-basic"
                  label="Confirmation"
                  type={showConfirmation ? 'text' : 'password'}
                  variant="outlined"
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowConfirmation}
                        onMouseDown={handleMouseDownConfirmation}
                        edge="end"
                      >
                        {showConfirmation ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  }
                  value={inputs.confirmation || ""}
                  onChange={handleChange}
                  name="confirmation"
                />       
              </FormControl>


        </div>
        </DialogContent>
        <DialogActions sx={{ p: '1.25rem' }}>
          <Button color="error" onClick={handleClose} variant="contained">
            Cancel
          </Button>
          <Button color="primary" onClick={handleSubmit} variant="contained" disabled={inputs.confirmation!==inputs.password || inputs.password==="" || code===""}>
            Submit
          </Button>
        </DialogActions>
      </Dialog>
      </>
    );
}

export default SetNewPassword;
