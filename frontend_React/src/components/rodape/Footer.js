import React from "react";
//import logo from '../../assets/Novologofct2021.png';
import vars from "../../services/var/var";

const Footer = () => (
  <footer className="bg-light p-3 text-center">
    <p>
      Back-office by <a href="https://www.fct.unl.pt">{vars.nova_fct}</a>
    </p>
  </footer>
);

export default Footer;

/*
  <div>
    <img src={logo} width="5%" alt="ola"/>
  </div>
*/
