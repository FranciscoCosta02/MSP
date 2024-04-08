import React,{useState,useEffect} from 'react';
import {
  IconButton,
  Tooltip,
  ImageList,
  ImageListItem,
  ImageListItemBar,
  Backdrop,
  CircularProgress,
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useParams } from "react-router-dom";
import { apiToken,apiActivityImage } from '../../../services/api/api';
import vars from '../../../services/var/var';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import history from '../../../utils/history';
import Cookies from 'js-cookie';
import AlertComponent from '../../alerts/AlertComponent';


export default function Photos() {
  const {activitiID} = useParams();

  const [openBackDrop, setOpenBackDrop] = useState(false);
  const [itemData,setItemData] = useState([]);

  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };


  async function getData(){
    setOpenBackDrop(true);
    try{
      const {data} = await apiToken.get('/activity/photos?id='+activitiID);
      console.log("Activity photos");
      console.log(data);
      setItemData(data);
      setOpenBackDrop(false);
    }catch(error){
      setOpenBackDrop(false);
      setOpen({open:true,type:"error",text:"Error getting photos!"});
    }
  }

  async function deletePhoto(photoName){
    console.log(photoName)
    var tmp="activities/" ;
    var name_photo = photoName.substr(tmp.length, photoName.lenght);
    console.log(name_photo);
    if (
      // eslint-disable-next-line no-restricted-globals
      !confirm(`Are you sure you want to delete the photo: ${name_photo}`)
    ) {
      return;
    }
    try{
      await apiActivityImage.delete(name_photo,{headers:{'Authorization': 'Bearer ' + Cookies.get("loginToken")}});
      setOpen({open:true,type:"success",text:"Photo deleted with Success!"});
      itemData.splice(itemData.indexOf(photoName), 1);
    }
    catch(error){
      setOpen({open:true,type:"error",text:"Error deleting photos!"});
    }
    
  }

  useEffect(() => {
    getData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    activitiID
  ]);

  return (
    <>
        <Backdrop
          sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={openBackDrop}
        >
          <CircularProgress color="inherit" />
        </Backdrop>

        <Tooltip title="go back">
          <IconButton
            color="primary"
            onClick={() => {
              history.back();
            }}
          >
            <ArrowBackIcon />
          </IconButton>
        </Tooltip>

        <Tooltip title="Refresh data">
          <IconButton
            color="primary"
            onClick={() => {
              getData();
            }}
          >
            <RefreshIcon />
          </IconButton>
        </Tooltip>
    {
      itemData.length!==0?
      <>
      <ImageList sx={{ width: '100%', height: 500 }} cols={3}>
        {itemData.map((item) => (
          <ImageListItem key={item} >
            <img
              src={vars.bucket+item+`?w=164&h=164&fit=crop&auto=format`}
              srcSet={vars.bucket+item+`?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
              alt="error"
              loading="lazy"
            />
            <ImageListItemBar
                title={item}
                actionIcon={
                  <>
                  <IconButton
                    sx={{ color: 'rgba(255, 255, 255, 0.54)' }}
                    aria-label={`info about ${item}`}
                    onClick={() => deletePhoto(item)}
                  >
                    <DeleteForeverIcon />
                  </IconButton>
                  </>
                }
              />
          </ImageListItem>
          
        ))}
      </ImageList>
      </>
      :
      <>
      <p>No photos available</p>
      </>
    }

    <AlertComponent
        openModal={open.open}
        type={open.type}
        text={open.text}
        onClose={handleClose}
      />
    
    </>
  );

}
