import React,{useState,useEffect} from "react";
import {apiToken} from "../../../../services/api/api";
import InputAdornment from '@mui/material/InputAdornment';
import FormControl from '@mui/material/FormControl';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import { 
  Modal,
  Typography,
  Box,
  TextField,
  Button,
  MenuItem,
  IconButton,
  Select,
  OutlinedInput,
  InputLabel,
} from '@mui/material';

function ChangePassWordModal(props){
    const {open,onClose,SuccessAlert,ErrorAlert,row} = props
  const [showPasswordConfirmation, setShowPasswordConfirmation] = useState(false);
  const [showPasswordNewPwd, setShowPasswordNewPwd] = useState(false);
  
  useEffect(() => {
    setInputs({
      password:"",confirmation:"",privacy:row.privacy || "",name:row.name || ""
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    row.name
  ]);


  const handleCloseModal = () => {
    onClose();
    setShowPasswordConfirmation(false);
    setShowPasswordNewPwd(false);
    setInputs({
      password:"",confirmation:"",privacy:row.privacy || "",name:row.name || ""
    });
  };

  const [inputs, setInputs] = useState({
    password:"",confirmation:"",privacy:row.privacy || "",name:row.name || ""
  });
  const handleChangePWDform = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setInputs(values => ({...values, [name]: value}))
  }

  function handleSubmit(e){
    e.preventDefault();
    if(inputs.privacy==="Private"){
      let pattern1=/[^a-z0-9 ]/g;
      let pattern2=/[0-9 ]/g;
      let pattern3=/[A-Z ]/g;
      let pattern4=/[a-z ]/g;
      let pwd = inputs.password;
      if(pwd.length<7){
        ErrorAlert("your new password must have at least 7 characters");
        return null;
      }
      if(pattern1.test(pwd) && pattern2.test(pwd) && pattern3.test(pwd) && pattern4.test(pwd)){
        Submit();
      }
      else{
        ErrorAlert("The password must contain numbers, special characters, upperCaseLetters and lowerCaseLetters");
      }
    }
    else if(inputs.privacy==="Public"){
      Submit();
    }
  }

  async function Submit(){
    console.log(inputs);
    try{
      const {data} = await apiToken.put('/group',inputs);
      console.log(data);
      SuccessAlert("Password changed with success!!")
      handleCloseModal();
    }
    catch(error){
      console.log(error);
      ErrorAlert("Error changing password!!")
    }
  }
  

  const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 450,
    bgcolor: 'background.paper',
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
                Change Group Password: <br/>
                {row.name || ""}
                <br/>
              </Typography>
              <form onSubmit={handleSubmit}>

              <FormControl fullWidth style={FormPsswordStyle}>
              <InputLabel id="demo-simple-select-label">Privacy</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={inputs.privacy}
                label="Privacy"
                name="privacy"
                onChange={handleChangePWDform}
              >
                <MenuItem value="Public">Public</MenuItem>
                <MenuItem value="Private">Private</MenuItem>
              </Select>
            </FormControl>
              { inputs.privacy!=="Public" &&
              <>
              <FormControl  fullWidth className="loginFormTextF" variant="outlined" style={FormPsswordStyle}>
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
                    value={inputs.password || ""}
                    onChange={handleChangePWDform}
                    name="password"
                />       
                </FormControl> 
                <FormControl fullWidth className="loginFormTextF" variant="outlined" style={FormPsswordStyle}>
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
                </>} 
              <br/>
              <Button disabled={
                inputs.privacy==="Private" && (
                inputs.confirmation==="" ||
                inputs.password==="" ||
                inputs.password!==inputs.confirmation)
                } variant="contained" type="submit">Submit change</Button>

              </form>
            </Box>
          </Modal>
          
    </div>
  );
};

export default ChangePassWordModal;
