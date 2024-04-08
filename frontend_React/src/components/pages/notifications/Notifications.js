import React,{useState,useEffect} from 'react';
import PropTypes from 'prop-types';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import GroupNotifications from './GroupNotifications';
import IndividualNotifications from './IndividualNotifications';
import SentNotifications from './SentNotifications';
import {apiToken} from '../../../services/api/api';
import OutlinedInput from '@mui/material/OutlinedInput';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import CircularProgress from '@mui/material/CircularProgress';
import Backdrop from '@mui/material/Backdrop';

import Cookies from "js-cookie";
import vars from "../../../services/var/var";
import AlertComponent from '../../alerts/AlertComponent';

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
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
  };
}

export default function BasicTabs() {
  const [value, setValue] = useState(0);
  const [groups, setGroups] = useState([]);
  const [groupName, setGroupName] = useState([]);
  const [openBackDrop, setOpenBackDrop] = useState(false);

  //Alert
  const [open, setOpen] = useState({open:false,type:"error",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"error",text:""});
    
  };

  //groupValue

  const [groupValue, setGroupValue] = useState("ALL");

  const handleChangeRadioGroupV = (event) => {
    setGroupValue(event.target.value);
  };

  const handleChangePage = (event, newValue) => {
    setValue(newValue);
  };

  const handleChange = (event) => {
    const {
    target: { value },
    } = event;
    setGroupName(
      // On autofill we get a stringified value.
      typeof value === 'string' ? value.split(',') : value,
    );
    console.log(groupName);
};

  useEffect(() => {
    
    const fetchData = async () => {
      setOpenBackDrop(true);
      if(!Cookies.get(vars.roles)){
        try {
          const {data} = await apiToken.get('/get/roles');
          console.log(data);
          Cookies.set(vars.roles, JSON.stringify(data),{ expires: vars.roles_expires});
          setGroups(data);
        } catch (error) {
          console.error("Error: "+error);
          setOpen({open:true,type:"error",text:vars.alerts.notifications.no_roles});
        }
      }
      else{
        setGroups(JSON.parse(Cookies.get(vars.roles)));
      }
      setOpenBackDrop(false);
    };

    fetchData();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, 
  [
    groups.lenght,
  ]
  );

  

  return (
    <>
        <Backdrop
          sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={openBackDrop}
        >
          <CircularProgress color="inherit" />
        </Backdrop>
        <Box sx={{ width: '100%' }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={value} onChange={handleChangePage} aria-label="basic tabs example">
              <Tab label="Send Notifications (Roles)" {...a11yProps(0)} />
              <Tab label="Send Notifications (Individual)" {...a11yProps(1)} />
              <Tab label="Notifications List (Sent)" {...a11yProps(2)} />
            </Tabs>
          </Box>
          <TabPanel value={value} index={0}>
          <FormControl sx={{ width: 500 }} >
            <InputLabel id="demo-multiple-checkbox-label">Groups</InputLabel>
            <Select
              labelId="demo-multiple-checkbox-label"
              id="demo-multiple-checkbox"
              multiple
              value={groupName}
              onChange={handleChange}
              input={<OutlinedInput label="Roles" />}
              renderValue={(selected) => selected.join(', ')}
              MenuProps={MenuProps}
            >
              {groups.map((name) => (
                <MenuItem key={name} value={name}>
                  <Checkbox checked={groupName.indexOf(name) > -1} />
                  <ListItemText primary={name} />
                </MenuItem>
              ))}
            </Select>
            </FormControl>
            <GroupNotifications groupName={groupName}/>
          </TabPanel>
          <TabPanel value={value} index={1}>
          <FormControl component="fieldset">
            <FormLabel component="legend">Search Filter</FormLabel>
                <RadioGroup aria-label="position" row
                value={groupValue}
                onChange={handleChangeRadioGroupV}>
                <FormControlLabel
                  value="ALL"
                  control={<Radio/>}
                  label="ALL"
                  labelPlacement="end"
                />
                {
                  groups.map((elem)=>
                  (
                  <FormControlLabel
                  value={elem}
                  control={<Radio/>}
                  label={elem}
                  labelPlacement="end"
                />))}
                </RadioGroup>
            </FormControl>
            <IndividualNotifications groupValue={groupValue}/>
          </TabPanel>
          <TabPanel value={value} index={2}>
            <SentNotifications/>
          </TabPanel>
        </Box>
        <AlertComponent
            openModal={open.open}
            type={open.type}
            text={open.text}
            onClose={handleClose}
          />
    </>
  );
}