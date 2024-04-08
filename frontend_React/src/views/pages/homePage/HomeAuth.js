import React, { Fragment } from "react";

import Welcome from "../../../components/pages/homePage/Welcome";
import vars from "../../../services/var/var";


const HomeAuth = () => (
  <Fragment>
    <Welcome />
    <hr />
    <div className="text-center hero my-5">
      <h4 className="mb-4" style={{color:"#0033cc"}}>{localStorage.getItem(vars.user_role)} ACCOUNT</h4>
    </div>
  </Fragment>
);

export default HomeAuth;
