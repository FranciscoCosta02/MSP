import React,{useState} from "react";
import {apiToken} from "../../../../services/api/api";
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import InputAdornment from '@mui/material/InputAdornment';
import FormControl from '@mui/material/FormControl';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import { 
  Modal,
  Typography,
  Box,
  Button,
  IconButton,
} from '@mui/material';

function ChangePassWordModal(props){
    const {open,onClose,SuccessAlert,ErrorAlert} = props
  const [showPasswordConfirmation, setShowPasswordConfirmation] = useState(false);
  const [showPasswordNewPwd, setShowPasswordNewPwd] = useState(false);
  const [showPasswordOldPwd, setShowPasswordOldPwd] = useState(false);
  
  


  const handleCloseModal = () => {
    setInputs({});
    onClose();
  };

  const [inputs, setInputs] = useState({
    oldPwd:"",newPwd:"",confirmation:""
  });
  const handleChangePWDform = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setInputs(values => ({...values, [name]: value}))
  }

  async function handleSubmit(e){
    e.preventDefault();
    console.log(inputs);
    let pattern1=/[^a-z0-9 ]/g;
    let pattern2=/[0-9 ]/g;
    let pattern3=/[A-Z ]/g;
    let pattern4=/[a-z ]/g;
    let pwd = inputs.newPwd;
    if(pwd.length<7){
      ErrorAlert("your new password must have at least 7 characters");
      return null;
    }
    if(pattern1.test(pwd) && pattern2.test(pwd) && pattern3.test(pwd) && pattern4.test(pwd)){
      try{
        const {data} = await apiToken.put('/update/password',inputs);
        console.log(data);
        SuccessAlert("Password changed with success!!")
      }
      catch(Error){
        console.log(Error);
        ErrorAlert("Error changing password!!")
      }
    }
    else{
      ErrorAlert("The password must contain numbers, special characters, upperCaseLetters and lowerCaseLetters");
    }
    
  }

  

  const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 450,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

  const FormPsswordStyle = {
    marginTop: '10px',
    marginBottom: '5px',
  };

  const handleClickShowPasswordConfirmation = () => setShowPasswordConfirmation((show) => !show);

  const handleMouseDownPasswordConfirmation = (event) => {
    event.preventDefault();
  };

  const handleClickShowPasswordnewPwd = () => setShowPasswordNewPwd((show) => !show);

  const handleMouseDownPasswordnewPwd = (event) => {
    event.preventDefault();
  };
  const handleClickShowPasswordoldPwd = () => setShowPasswordOldPwd((show) => !show);

  const handleMouseDownPasswordoldPwd = (event) => {
    event.preventDefault();
  };


  

  return (
    <div>
          <Modal
            open={open}
            onClose={handleCloseModal}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
          >
            <Box sx={style}>
              <Typography id="modal-modal-title" variant="h6" component="h2">
                Change Password
              </Typography>
              <form onSubmit={handleSubmit}>
        
              <FormControl className="loginFormTextF" variant="outlined" style={FormPsswordStyle}>
                <InputLabel >Old password</InputLabel>
                <OutlinedInput
                    id="outlined-basic"
                    label="Password"
                    
                    type={showPasswordOldPwd ? 'text' : 'password'}
                    variant="outlined"
                    endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPasswordoldPwd}
                        onMouseDown={handleMouseDownPasswordoldPwd}
                        edge="end"
                        >
                        {showPasswordOldPwd ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                    </InputAdornment>
                    }
                    value={inputs.oldPwd || ""}
                    onChange={handleChangePWDform}
                    name="oldPwd"
                />       
                </FormControl> 
              <FormControl className="loginFormTextF" variant="outlined" style={FormPsswordStyle}>
                <InputLabel >New password</InputLabel>
                <OutlinedInput
                    id="outlined-basic"
                    label="Password"
                    type={showPasswordNewPwd ? 'text' : 'password'}
                    variant="outlined"
                    endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPasswordnewPwd}
                        onMouseDown={handleMouseDownPasswordnewPwd}
                        edge="end"
                        >
                        {showPasswordNewPwd ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                    </InputAdornment>
                    }
                    value={inputs.newPwd || ""}
                    onChange={handleChangePWDform}
                    name="newPwd"
                />       
                </FormControl> 
                <FormControl className="loginFormTextF" variant="outlined" style={FormPsswordStyle}>
                <InputLabel >New password confirmation</InputLabel>
                <OutlinedInput
                    id="outlined-basic"
                    label="Password"
                    type={showPasswordConfirmation ? 'text' : 'password'}
                    variant="outlined"
                    endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPasswordConfirmation}
                        onMouseDown={handleMouseDownPasswordConfirmation}
                        edge="end"
                        >
                        {showPasswordConfirmation ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                    </InputAdornment>
                    }
                    value={inputs.confirmation || ""}
                    onChange={handleChangePWDform}
                    name="confirmation"
                />       
                </FormControl>  
              <br/>
              <Button disabled={
                inputs.confirmation==="" ||
                inputs.newPwd==="" ||
                inputs.oldPwd==="" || 
                inputs.newPwd!==inputs.confirmation || inputs.newPwd===inputs.oldPwd
                } variant="contained" type="submit">Submit change</Button>

              </form>
            </Box>
          </Modal>
          
    </div>
  );
};

export default ChangePassWordModal;
