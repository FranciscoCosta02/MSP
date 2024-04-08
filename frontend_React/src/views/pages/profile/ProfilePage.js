import React,{useState,useEffect} from "react";
import { Container, Row, Col } from "reactstrap";

import userDefaultPhoto from "../../../assets/userDefaultPhoto.png";
import {apiToken,apiUserImage} from "../../../services/api/api";
import Cookies from "js-cookie";
import vars from "../../../services/var/var";
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';

import { 
  Input,
  Switch,
  FormHelperText,
  FormControlLabel,
  FormGroup,FormControl,
  FormLabel,
  IconButton,
  Checkbox,
  TextField,
  Stack,
  Backdrop,
  CircularProgress,
  Button,
} from '@mui/material';
import ChangePassWordModal from "./modal/ChangePassWordModal";
import AlertComponent from "../../../components/alerts/AlertComponent";

function ProfileComponent(){
  const [show, setShow] = useState(false);
  const [user, setUserData] = useState({username:localStorage.getItem("username"),role:localStorage.getItem("role"),photo:"",privacy:""});
  const [openBackDrop, setOpenBackDrop] = useState(false);
  const [disabledFields, setDisabledFields] = useState(true);
  const [file, setFile] = useState(null);
  const [visibility, setVisibility] = useState(false);

  const [openModal, setOpenModal] = useState(false);
  const handleOpenModal = () => {
    setOpenModal(true);
    setVisibility(false);
  };
  const handleCloseModal = () => {
    setOpenModal(false);
  };

  //Alert
  const [open, setOpen] = useState({open:false,type:"error",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"error",text:""});
    
  };


  useEffect(() => {
    var tmp = localStorage.getItem("user_data");
    if(tmp!=null){
      setUserData(JSON.parse(tmp));
      setShow(true);
    }
    else{
      getUser();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [

  ]);
  
 
  function changeVisibility(){
    if(visibility)
      setVisibility(false);
    else
      setVisibility(true);
  }


  const [groupNames, setGroupNames] = useState([
    'GUEST',
    'STUDENT',
    'STAFF',
    'SU',
  ]);


  async function getRoles(){
    if(!Cookies.get(vars.roles)){
      try {
        const {data} = await apiToken.get('/get/roles');
        console.log(data);
        Cookies.set(vars.roles, JSON.stringify(data),{ expires: vars.roles_expires});
        setGroupNames(data);
      } catch (error) {
        console.error("Error: "+error);
        
      }
    }
    else{
      setGroupNames(JSON.parse(Cookies.get(vars.roles)));
    }
    
  }

  async function getUser(){
    try{
      if(user.email===null || user.email===undefined)
        setOpenBackDrop(true);
      const {data} = await apiToken.get("/get");
      console.log("USER DATA: ");
      console.log(data);
      localStorage.setItem("user_data",JSON.stringify(data));
      setUserData(data);
      setPrivacySetting(data.privacy,data.groups);      
      getRoles();
      setShow(true);
      setOpenBackDrop(false);
    }
    catch(Error){
      setOpenBackDrop(false);
      ErrorAlert("Error modifying user");
      console.log(Error);
    }
    
  }

  const handleFileChange = (e) => {
    console.log(e);
    if (e.target.files) {
      setFile(e.target.files[0]);
    }
  };
  
  function setGroupsOnUser(){
    var txt="";
    Object.keys(groups).forEach(function(item){
      if(groups[item]){
        if(txt===""){
          txt+=item;
        }
        else{
          txt+=";"+item;
        }
      }
      console.log(item + " - " + groups[item]);
     });
    return txt;
  }

  function setPrivacyAttributesOnUser(){
    var txt="";
    Object.keys(state).forEach(function(item){
      if(state[item]){
        if(txt===""){
          txt+=item;
        }
        else{
          txt+=";"+item;
        }
      }
      console.log(item + " - " + state[item]);
     });
    return txt;
  }

  async function SubmitChanges(){
    setDisabledFields(true);
    console.log("Changes: "+user);
    console.log(user);
    var privacy = setPrivacyAttributesOnUser();
    var groups = setGroupsOnUser();
    var newUserVersion = {};
    newUserVersion.privacy=privacy;
    newUserVersion.groups=groups;
    newUserVersion.activity=user.activity;
    newUserVersion.email=user.email;
    newUserVersion.password=user.password;
    newUserVersion.department=user.department;
    newUserVersion.confirmation=user.confirmation;
    newUserVersion.name=user.name;
    newUserVersion.role=user.role;
    newUserVersion.username=user.username;
    newUserVersion.phone=user.phone;
    newUserVersion.photo=user.photo;

    console.log(newUserVersion);
    if(file!==null){
      var ext = file.name.split(".")[1];
      var RandNum = getRandomArbitrary(0,100000);
      console.log("RANDNUM: "+RandNum);
      newUserVersion.photo=newUserVersion.username+RandNum+"."+ext
    }
    try{
      await apiToken.put('/update/attributes',newUserVersion);
      SuccessAlert("User updated withh success");
      if(file!==null){
        var tmpPhoto = user.photo;
        saveNewPhoto(newUserVersion,tmpPhoto);
      }
      else{
        setUserData(newUserVersion);
        localStorage.setItem("user_data",JSON.stringify(newUserVersion));
      }
    }
    catch(error){
      console.log(error);
      ErrorAlert("Error modifying user");

    }
    
  }

  function getRandomArbitrary(min, max) {
    return Math.floor(Math.random() * (max - min) + min);
  }

  async function saveNewPhoto(newUserVersion,oldPhotoName){
    var photoName=newUserVersion.photo
    console.log(photoName);
    console.log(oldPhotoName);
    try{
      await apiUserImage.post(photoName+"/"+oldPhotoName,file,{headers:{'Content-Type': ''+file.type,'Authorization': 'Bearer ' + Cookies.get("loginToken")}});
      console.log("FOTO SUBMETIDA COM SUCESSO: ");
      setUserData(newUserVersion);
      localStorage.setItem("user_data",JSON.stringify(newUserVersion));
    }
    catch(error){
      ErrorAlert("ERROR SAVING PHOTO")
      console.log(error);
    }
  }

  function Cancel(){
    setDisabledFields(true);
    var tmp = localStorage.getItem("user_data");
    setUserData(JSON.parse(tmp));
    setVisibility(false);
  }

  const styleRow = {
    padding: "10px",
  }

  function ChangeData(){
    setDisabledFields(false);
    console.log(user);
  }

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setUserData(values => ({...values, [name]: value}))
  }

  function SuccessAlert(text){
    setOpen({open:true,type:"success",text:text});

  }

  function ErrorAlert(text){
    setOpen({open:true,type:"error",text:text});
  }


  const setPrivacySetting = (list,groups) =>{
    var tmp = state;
    var newList = list.split(";");
    var i=0;
    for(i=0;i<newList.length;i++){
      tmp[newList[i]]=true;
    }
    setState(tmp);

    var tmp2 = {};
    newList = groups.split(";");
    
    for(i=0;i<newList.length;i++){
      tmp2[newList[i]]=true;
    }
    setGroups(tmp2);
  }


  const [state, setState] = useState({
    phone: false,
    email: false,
    role: false,
    department: false,
  });

  const handleChangeSwitch = (event) => {
    setState({
      ...state,
      [event.target.name]: event.target.checked,
    });
    console.log("switch");
    console.log(state);
  };

  const [groups, setGroups] = useState({});


  const handleChangeCheckBox = (event) => {
    setGroups({
      ...groups,
      [event.target.name]: event.target.checked,
    });
    console.log("groups");
    console.log(groups);
  };

  return (
    <>
    
    <Backdrop
      sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
      open={openBackDrop}
    >
      <CircularProgress color="inherit" />
    </Backdrop>

    <Container className="mb-5">
      <Row className="align-items-center profile-header mb-5 text-center text-md-left">
        <Col md={2}>
          <img
            src={user.photo!==""?(vars.bucket_users+user.photo): userDefaultPhoto}
            alt="Profile"
            className="rounded-circle img-fluid profile-picture d-none d-md-block"
          />
          <img
            src={user.photo!==""?(vars.bucket_users+user.photo): userDefaultPhoto}
            alt="Profile"
            width="40%"
            className="rounded-circle img-fluid profile-picture d-md-none"
          />
        </Col>
        <Col md>
          <h2>{user.username}</h2>
          <p className="lead text-muted">{user.role}</p>
        </Col>
      </Row>
      {show &&
      (<>
          <Stack direction="row" spacing={2} style={styleRow}>
            <TextField id="outlined-basic" label="Username" variant="outlined" value={user.username} disabled/>
            <TextField id="outlined-basic" label="Name" name="name" variant="outlined" onChange={handleChange} value={user.name} disabled={disabledFields}/>
          </Stack>

          <Stack direction="row" spacing={2} style={styleRow}>
            <TextField id="outlined-basic" label="Email" name="email" variant="outlined" value={user.email} onChange={handleChange} disabled={disabledFields}/>
            <TextField id="outlined-basic" label="Phone number" name="phone" variant="outlined" value={user.phone} onChange={handleChange} disabled={disabledFields}/>
          </Stack>

          <Stack direction="row" spacing={2} style={styleRow}>
            <TextField id="outlined-basic" label="Department" name="department" onChange={handleChange} variant="outlined" value={user.department} disabled={disabledFields}/>
            <TextField id="outlined-basic" label="Role" variant="outlined" value={user.role} disabled/>
          </Stack>
          

          {!disabledFields &&
          <>
            <Stack direction="row" spacing={2} style={styleRow}>
              <Input type="file" accept=".jpg"  onChange={handleFileChange}/>
            </Stack>
            <Stack direction="row" spacing={2} style={styleRow}>
              More settings:
              <IconButton aria-label="more" onClick={changeVisibility}>
                <MoreHorizIcon />
              </IconButton>
            </Stack>
              
          
          <Row className="align-items-center profile-header mb-5 text-center text-md-left">
          {visibility && 
          <>
            <Col md={6}>
              
              <FormControl component="fieldset" sx={{ m: 6 }} variant="standard" >
                <FormLabel component="legend">Privacy settings</FormLabel>
                <FormGroup>
                  <FormControlLabel
                    control={
                      <Switch checked={state.phone} onChange={handleChangeSwitch} name="phone" />
                    }
                    label="Phone"
                  />
                  <FormControlLabel
                    control={
                      <Switch checked={state.email} onChange={handleChangeSwitch} name="email" />
                    }
                    label="Email"
                  />
                  <FormControlLabel
                    control={
                      <Switch checked={state.role} onChange={handleChangeSwitch} name="role" />
                    }
                    label="Role"
                  />
                  <FormControlLabel
                    control={
                      <Switch checked={state.department} onChange={handleChangeSwitch} name="department" />
                    }
                    label="Department"
                  />
                </FormGroup>
                <FormHelperText>Turn on the Switch if you want to make a user property private </FormHelperText>
              </FormControl>
              
            </Col>
            <Col md>
              <FormControl sx={{ m: 6 }} component="fieldset" variant="standard">
                <FormLabel component="legend">User groups who can see your private properties</FormLabel>
                <FormGroup>
                {groupNames.map((elem)=>
                  <FormControlLabel
                    control={
                      <Checkbox checked={groups[elem]} onChange={handleChangeCheckBox} name={elem} />
                    }
                    label={elem}
                  />
                )}
                </FormGroup>
                <FormHelperText>Be careful, if you select a group all elements of that group will be able to see your private properties</FormHelperText>
              </FormControl>
            </Col>
            </>
            }
          </Row>
          </>
          }

          <Stack direction="row" spacing={2} style={styleRow}>
            {disabledFields && <Button variant="contained" onClick={ChangeData} disabled={!disabledFields}>Change Data</Button>}
            {!disabledFields && <Button variant="contained" onClick={Cancel} disabled={disabledFields}>Cancel</Button>}
            {!disabledFields && <Button variant="contained" onClick={SubmitChanges} disabled={disabledFields}>Submit Changes</Button>}
            {disabledFields && <Button variant="contained" onClick={handleOpenModal} disabled={!disabledFields}>Change Password</Button>}
          </Stack>

          
      </>
      )}

    </Container>
    
          <AlertComponent
            openModal={open.open}
            type={open.type}
            text={open.text}
            onClose={handleClose}
          />
          <ChangePassWordModal
            open={openModal}
            onClose={handleCloseModal}
            SuccessAlert={SuccessAlert}
            ErrorAlert={ErrorAlert}
          />
          
    </>
  );
};

export default ProfileComponent;
