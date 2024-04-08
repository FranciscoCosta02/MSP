import React, { useState ,useEffect} from 'react';

import ListUsersPage from "../../../components/pages/listUsers/ListUsers";

import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import {apiToken} from '../../../services/api/api';
import vars from '../../../services/var/var';

import CircularProgress from '@mui/material/CircularProgress';
import Backdrop from '@mui/material/Backdrop';
import Cookies from "js-cookie";

  
const ListUsers = () => {
  const [userType, setUserType] = useState("");
  const [groups, setGroups] = useState([]);
  const [openBackDrop, setOpenBackDrop] = useState(false);


  const handleChange = (event) => {
    setUserType(event.target.value);
  };


  useEffect(() => {
    
    const fetchData = async () => {
      setOpenBackDrop(true);
      if(!Cookies.get(vars.roles)){
      try {
        const {data} = await apiToken.get('/get/roles');
        console.log(data);
        Cookies.set(vars.roles, JSON.stringify(data),{ expires: vars.roles_expires});
        setGroups([...data,"ALL"]);
      } catch (error) {
        console.error("Error: "+error);
      }
      }
      else{
        setGroups([...JSON.parse(Cookies.get(vars.roles)),"ALL"]);
      }
      setOpenBackDrop(false);
      return;

    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, 
  []
  );
  
  return(
    <>
    <h1>List users {userType}</h1>
        <Backdrop
          sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={openBackDrop}
        >
          <CircularProgress color="inherit" />
        </Backdrop>
    <FormControl fullWidth>
      <InputLabel id="demo-simple-select-label">User type</InputLabel>
      <Select
        labelId="demo-simple-select-label"
        id="demo-simple-select"
        value={userType}
        label="User type"
        onChange={handleChange}
      >
        {groups.map((elem)=>
          <MenuItem value={elem}>{elem}</MenuItem>
        )}

      </Select>
    </FormControl>
    {
      userType===""?
      <></>:
      <ListUsersPage userType={userType}/>
    }
    
    </>
  );
};

export default ListUsers;
