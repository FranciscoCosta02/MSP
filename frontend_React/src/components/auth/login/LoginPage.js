import React,{useState} from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Cookies from 'js-cookie';
import {apiLogin} from "../../../services/api/api";
import vars,{endpoint, path} from "../../../services/var/var";
import {Link} from "react-router-dom";
import CircularProgress from '@mui/material/CircularProgress';
import Backdrop from '@mui/material/Backdrop';
import './AlertStyle.css';
import IconButton from '@mui/material/IconButton';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import InputAdornment from '@mui/material/InputAdornment';
import FormControl from '@mui/material/FormControl';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import AlertComponent from '../../alerts/AlertComponent';


const LoginPage = () => {

  const [inputs, setInputs] = useState({});
  const [disabledBtn, setDisabledBtn] = useState(false);
  const [openBackDrop, setOpenBackDrop] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  //Alert
  const [open, setOpen] = useState({open:false,type:"error",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"error",text:""});
    
  };

  function startResquest(){
      setOpenBackDrop(true);
      setDisabledBtn(true);
      handleClose()
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
    if(inputs.username===undefined || inputs.username==="" || inputs.password===undefined || inputs.password==="" || inputs.username.trim()==="" || inputs.password.trim()===""){
      setOpen({open:true,type:"error",text:vars.alerts.auth.empty_textFields});
      return null;
    }
    try{
      startResquest();
      const { data } = await apiLogin.post(endpoint.login, inputs);
      Cookies.set(vars.loginToken, data[0],{ expires: vars.loginToken_expiration_time});
      localStorage.setItem(vars.username,data[1]);
      localStorage.setItem(vars.user_role,data[2]);
      console.log(data);
      finnishedResquest();
      window.location.href=path.home;
    }
    catch(error){
      finnishedResquest();
      setOpen({open:true,type:"error",text:vars.alerts.auth.login_error});
      console.log("Error: "+error)
      console.log(error)


    }
    
  }

  const divStyle = {
    padding: "10px",
  };

  const handleClickShowPassword = () => setShowPassword((show) => !show);

  const handleMouseDownPassword = (event) => {
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
              <TextField className="loginFormTextF" id="outlined-basic" label="Username" name="username" value={inputs.username || ""} onChange={handleChange} variant="outlined" />
            </div> 
            <div style={divStyle}> 
            <FormControl className="loginFormTextF" variant="outlined">
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
                value={inputs.password || ""}
                onChange={handleChange}
                name="password"
              />       
            </FormControl>       
            </div>  
            <div >
              <Button disabled={disabledBtn} variant="contained" type="submit" className="loginBtn">Login</Button>
            </div>
          </form>
          <div className="space">
            <Link to={path.recoverPWD}>Recover password</Link>
          </div>
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

export default LoginPage;
