import React, { Fragment } from "react";
import PhotosPage from "../../../components/pages/photos/Photos";
import { useParams } from "react-router-dom";





const Photos = () => {
  const {activitiID,actTitle} = useParams();
  return(
      <Fragment>
        <h1>Activity: {actTitle}</h1>
        <PhotosPage activitiID={activitiID}/> 

      </Fragment>
  );
}

export default Photos;

