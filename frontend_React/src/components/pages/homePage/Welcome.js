import React from "react";
import vars from "../../../services/var/var";

//<img className="mb-3 app-logo" src={novaLogo} alt="React logo" width="120" />
const Welcome = () => (
  <div className="text-center hero my-5">
    
    <h1 className="mb-4">{vars.nova_fct} Back-Office</h1>

    <p className="lead">
    Welcome to the {vars.app_name}, to visit the college website click on <a href="https://www.fct.unl.pt">{vars.fct}</a>
    </p>
  </div>
);

export default Welcome;
