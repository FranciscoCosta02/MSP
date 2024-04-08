import React,{useState} from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import history from "../../../utils/history";
import {apiLogin} from './../../../services/api/api';
import AlertComponent from './../../alerts/AlertComponent';
import vars from "../../../services/var/var";
import {Link} from "react-router-dom";
import './../login/AlertStyle.css';
import SetNewPassword from "../setNewPassword/SetNewPassword";


const ResetPassword = () => {
  
  //Email
  const[email,setEmail]=useState("");

  //Alert
  const [open, setOpen] = useState({open:false,type:"error",text:""});
  const [openModal, setOpenModal] = useState(false);

  const handleClose = () => {
    setOpen({open:false,type:"error",text:""});
  };

  const handleCloseModal = () => {
    setOpenModal(false)
  };

  async function onSubmitNewPWD(inputs,code){
    console.log(inputs);
    console.log(code);
    console.log(email);
    try{
      console.log(email);
      const {data} = await apiLogin.put("/recover/check?code="+code+"&email="+email,inputs);
      console.log(data);
      setEmail("");
      handleCloseModal();
      setOpen({open:true,type:"success",text:vars.alerts.recoverPWD.success});
    }
    catch(error){
      console.log("Error: "+error);
      setOpen({open:true,type:"error",text:vars.alerts.recoverPWD.error});
    }

  }

  async function submitThis(e){
    e.preventDefault();
    if(email.trim()===""){
      setOpen({open:true,type:"error",text:vars.alerts.auth.no_email});
      return null;
    }
    try{
      console.log(email);
      const {data} = await apiLogin.post('/recover?email='+email);
      console.log(data);
      setOpen({open:true,type:"success",text:vars.alerts.auth.email_success});
      setOpenModal(true);
    }
    catch(error){
      console.log("Error: "+error);
      setOpen({open:true,type:"error",text:vars.alerts.auth.email_error});
    }
  }

  const back=()=>{
    history.back();
  }

  const divStyle = {
    padding: "10px",
  };

    return(
      <>
      <div className="text-center hero my-5">
          <form onSubmit={submitThis} className="loginForm"> 
            <div style={divStyle}> 
              <TextField className="loginFormTextF" id="outlined-basic" label="Email"  value={email} onChange={(e)=>setEmail(e.target.value)} variant="outlined" />
            </div>
            <div>
              <Button variant="contained" type="submit" className="loginBtn" disabled={email===""}>Send Email</Button>
            </div>
          </form>
          <div className="space">
            <Link onClick={back}>Back</Link>
          </div>
      </div>
      <AlertComponent
            openModal={open.open}
            type={open.type}
            text={open.text}
            onClose={handleClose}
      />

      <SetNewPassword
        open={openModal}
        onClose={handleCloseModal}
        onSubmit={onSubmitNewPWD}
      />
      </>
    );
}

export default ResetPassword;
