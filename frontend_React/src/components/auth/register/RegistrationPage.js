import React,{useState} from "react";
import {apiToken} from "../../../services/api/api";
import vars,{endpoint, path, u} from "../../../services/var/var";
import {
  Backdrop,
  Select,
  MenuItem,
  CircularProgress,
  IconButton,
  OutlinedInput,
  InputLabel,
  InputAdornment,
  FormControl,
  TextField,
  Button,
} from '@mui/material';
import {VisibilityOff,Visibility} from '@mui/icons-material';
import AlertComponent from '../../alerts/AlertComponent';


const RegisterPage = () => {

  const [inputs, setInputs] = useState(
    {"username":"","name":"","email":"","password":"","confirmation":"","role":"","privacy":"","activity":"","phone":"","department":"","groups":"","photo":""}
  );
  const [disabledBtn, setDisabledBtn] = useState(false);
  const [openBackDrop, setOpenBackDrop] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordConfirmation, setShowPasswordConfirmation] = useState(false);
  //Alert
  const [open, setOpen] = useState({open:false,type:"error",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"error",text:""});
    
  };

  function startResquest(){
      setOpenBackDrop(true);
      setDisabledBtn(true);
      handleClose();
  }

  function finnishedResquest(){
    setDisabledBtn(false);
    setOpenBackDrop(false);
  }

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setInputs(values => ({...values, [name]: value}))
  }

  async function handleSubmit(e){
    e.preventDefault();
    console.log(inputs);
    try{
      startResquest();
      const { data } = await apiToken.post(endpoint.register, inputs);
      console.log(data);
      setOpen({open:true,type:"success",text:vars.alerts.auth.new_account_success});      
      finnishedResquest();
      setInputs({"username":"","name":"","email":"","password":"","confirmation":"","role":"","privacy":"","activity":"","phone":"","department":"","groups":"","photo":""});
    }
    catch(error){
      finnishedResquest();
      console.log(error)
      setOpen({open:true,type:"error",text:error.response.data});
    }
  }

  const divStyle = {
    padding: "10px",
  };

  const handleClickShowPassword = () => setShowPassword((show) => !show);

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const handleClickShowPasswordConfirmation = () => setShowPasswordConfirmation((show) => !show);

  const handleMouseDownPasswordConfirmation = (event) => {
    event.preventDefault();
  };

    return(
      <>
      <div>
        <Backdrop
          sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={openBackDrop}
        >
          <CircularProgress color="inherit" />
        </Backdrop>
      </div>
      <div className="text-center hero my-5">
          <form onSubmit={handleSubmit} className="loginForm"> 
            <div style={divStyle}> 
                <TextField className="loginFormTextF" label="Username" name="username" value={inputs.username || ""} onChange={handleChange} variant="outlined" />
            </div> 
            <div style={divStyle}> 
                <TextField className="loginFormTextF"  label="Name" name="name" value={inputs.name || ""} onChange={handleChange} variant="outlined" />
            </div>
            <div style={divStyle}> 
                <TextField className="loginFormTextF"  label="Email" name="email" value={inputs.email || ""} onChange={handleChange} variant="outlined" />
            </div> 
            
            <div style={divStyle}> 
            <FormControl className="loginFormTextF" variant="outlined" helperText={(inputs.password==="")?"Campo de preenchhimento obrigatório":""}>
              <InputLabel >Password</InputLabel>
              <OutlinedInput
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
                value={inputs.password || ""}
                onChange={handleChange}
                name="password"
              />       
            </FormControl>
            </div> 
            <div style={divStyle}> 
            <FormControl className="loginFormTextF" variant="outlined">
              <InputLabel >Confirmation</InputLabel>
              <OutlinedInput
                label="Confirmation"
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
                onChange={handleChange}
                name="confirmation"
              />       
            </FormControl>       
            </div>  
            <div style={divStyle}> 
              <TextField className="loginFormTextF" id="outlined-basic" label="Phone" name="phone" value={inputs.phone || ""} onChange={handleChange} variant="outlined" />
            </div> 
            <div style={divStyle}>   
              <FormControl className="loginFormTextF" variant="outlined">
                <InputLabel id="demo-simple-select-label">Department</InputLabel>
                <Select
                  labelId="demo-simple-select-label"
                  id="demo-simple-select"
                  value={inputs.department || ""}
                  label="Department"
                  name="department"
                  onChange={handleChange}
                >
                  {vars.mock.departmentsLogin.map((elem)=>   
                      <MenuItem value={elem} disabled={elem===u.superUser}>{elem}</MenuItem>            
                  )}
      
                </Select>
              </FormControl>
            </div> 
            <div >
              <Button variant="contained" type="submit" className="loginBtn" 
              disabled={disabledBtn || inputs.name.trim()==="" || inputs.username.trim()==="" || inputs.password.trim()==="" || inputs.confirmation!==inputs.password}
                >Register</Button>
            </div>
          </form>

          <AlertComponent
            openModal={open.open}
            type={open.type}
            text={open.text}
            onClose={handleClose}
          />
      </div>
    </>
    );
}

export default RegisterPage;
