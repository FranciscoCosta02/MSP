import React, { useState } from "react";
import {Link} from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEnvelope,faGear } from "@fortawesome/free-solid-svg-icons";
import Badge from '@mui/material/Badge';
import {endpoint, path} from '../../services/var/var';
import logo from '../../assets/novaLogo.ico'
import {
  Collapse,
  Container,
  Navbar,
  NavbarToggler,
  NavbarBrand,
  Nav,
  NavItem,
  Button,
  UncontrolledDropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
} from "reactstrap";
import userDefaultPhoto from './../../assets/userDefaultPhoto.png'
import Cookies from 'js-cookie';
import {apiToken} from "../../services/api/api";
import validate from "../../services/var/validationFunc";
import vars from "../../services/var/var";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';


var isAuthenticated=Cookies.get("loginToken");

var username = localStorage.getItem("username");

function cleanALL(){
  Cookies.remove(vars.loginToken);
  Cookies.remove(vars.roles);
  localStorage.clear();
}

async function logoutWithRedirect(){
  cleanALL();
  try{
    await apiToken.delete(endpoint.logout);
    window.location.href=path.home;
  }
  catch(Error){
    alert("Error: "+Error)
    window.location.href=path.home;
  }
  
}


const mystyleNavItem = {
  padding:5,
};

const mystyleLink = {
  textDecoration: 'none',color: 'black',padding:5,
};

const NavBar = () => {
  const [isOpen, setIsOpen] = useState(false);

  const toggle = () => setIsOpen(!isOpen);

  return (
    
    <div className="nav-container">
      <Navbar color="light" light expand="md">
        
        <Container>
          <NavbarToggler onClick={toggle} />
          <Collapse isOpen={isOpen} navbar>
            
              <NavbarBrand >
                  <img
                    src={logo}
                    width="60px"
                    alt="LOGO SST"
                    className="d-none d-md-block"
                    style={{"marginLeft":"0px","marginRight":"0px","padding":"0px"}}
                  />
              </NavbarBrand>
          
            <Nav className="mr-auto" navbar>
            {isAuthenticated && (
                <Link
                  to={path.home}
                  style={mystyleLink}
                >
                  Home
                </Link>
             )}
              
              {validate.isUserAuthorized(vars.menu.faqs) && isAuthenticated && (
             
                <Link
                  to={path.faqs}
                  style={mystyleLink}
                >
                  FAQs
                  </Link>
              )}
              {validate.isUserAuthorized(vars.menu.myrooms) && isAuthenticated && (
                <Link
                  to={path.myRooms}
                  style={mystyleLink}
                >
                  Rooms
                </Link>
              )}
              {validate.isUserAuthorized(vars.menu.createActivities) && isAuthenticated && (
                <Link to={path.listActivities} style={mystyleLink}>
                    Activities
                </Link>
              )}
              { validate.isUserAuthorized(vars.menu.bugs) && isAuthenticated && (
                <Link to={path.bugList} style={mystyleLink}>
                    Reported Bugs
                </Link>
              )}
              { validate.isUserAuthorized(vars.menu.register) && isAuthenticated && (
                <Link to={path.register} style={mystyleLink}>
                    Register
                </Link>
              )}
              
              {isAuthenticated && (
                  <UncontrolledDropdown nav inNavbar style={mystyleNavItem}>
                  
                      <DropdownToggle nav caret id="profileDropDown">
                        Users
                      </DropdownToggle>
                  
                    <DropdownMenu>
                    {
                      validate.isUserAuthorized(vars.menu.listUsers) && 
                      <DropdownItem>
                        <Link to={path.listUsers} style={mystyleLink}>
                          List Users
                        </Link>
                        </DropdownItem>
                    }
                      {validate.isUserAuthorized(vars.menu.notifications) && isAuthenticated && (
                      <DropdownItem>
              
                        <Link
                          to={path.notifications}
                          style={mystyleLink}
                        >
                            Notifications
                        </Link>
                      </DropdownItem>
                      )}
                      {validate.isUserAuthorized(vars.menu.groups) && isAuthenticated && (
                      <DropdownItem>
                      <Link to={path.groups} style={mystyleLink}>
                        Groups
                      </Link>
                      </DropdownItem>
                      )}
                      {validate.isUserAuthorized(vars.menu.stats) && isAuthenticated && (
                      <DropdownItem>
                      <Link to={path.stats} style={mystyleLink}>
                        Statistics
                      </Link>
                      </DropdownItem>
                      )}
                      
                      </DropdownMenu>
                  </UncontrolledDropdown>
              )}
              {validate.isUserAuthorized(vars.menu.anomalies) && isAuthenticated && (
                  <UncontrolledDropdown nav inNavbar style={mystyleNavItem}>
                    <DropdownToggle nav caret id="profileDropDown">
                        Anomalies
                      </DropdownToggle>
                    <DropdownMenu>
                      <DropdownItem>
                      <Link to={path.toSolve} style={mystyleLink}>
                        To Solve
                      </Link>
                      </DropdownItem>
                      <DropdownItem>
                      <Link to={path.solved} style={mystyleLink}>
                        Solved
                      </Link>
                      </DropdownItem>
                      </DropdownMenu>
                  </UncontrolledDropdown>
              )}
            </Nav>

            <Nav className="d-none d-md-block" navbar>
              {!isAuthenticated && (
                  <Link to={path.login}>
                  <Button
                    id="qsLoginBtn"
                    color="primary"
                    className="btn-margin"
                  >
                    Log in
                  </Button>
                  </Link>
              )}
              {isAuthenticated && (
                <UncontrolledDropdown nav inNavbar>
                  <DropdownToggle nav caret id="profileDropDown">
                      <AccountCircleIcon/>
                  </DropdownToggle>
                  <DropdownMenu>
                    <DropdownItem header>{username}</DropdownItem>
                    <DropdownItem>
                      <Link to={path.myNotifications} style={mystyleLink}>
                          <FontAwesomeIcon icon={faEnvelope} /> 
                          My Notifications
                      </Link>
                    </DropdownItem>
                    <DropdownItem>
                      <Link to={path.profile} style={mystyleLink}>
                        <FontAwesomeIcon icon="user" className="mr-3" /> 
                        Profile
                      </Link>
                    </DropdownItem>
                    <DropdownItem>
                      <Link to={path.settings} style={mystyleLink}>
                        <FontAwesomeIcon icon={faGear} className="mr-3"/>
                        Settings
                      </Link>
                    </DropdownItem>
                    <DropdownItem
                      id="qsLogoutBtn"
                      onClick={() => logoutWithRedirect()}
                    >
                      <FontAwesomeIcon icon="power-off" className="mr-3" /> 
                      Log out
                    </DropdownItem>
                  </DropdownMenu>
                </UncontrolledDropdown>
              )}
            </Nav>
            
            {!isAuthenticated && (
              <Nav className="d-md-none" navbar>
                <Link to="/login">
                  <Button
                    id="qsLoginBtn"
                    color="primary"
                    block
                  >
                    Log in
                  </Button>
                </Link>
              </Nav>
            )}
            {isAuthenticated && (
              <Nav
                className="d-md-none justify-content-between"
                navbar
                style={{ minHeight: 170 }}
              >
                <NavItem>
                  <span className="user-info">
                    <h6 className="d-inline-block">{username}</h6>
                  </span>
                </NavItem>
                      
                  <Link
                    to={path.myNotifications}
                  >
                      <Badge badgeContent={4} color="primary" className="mr-3">
                       <FontAwesomeIcon icon={faEnvelope} /> 
                      </Badge>
                    My Notifications
                  </Link>
                  
                  <Link
                    to={path.profile}
                  >
                    <FontAwesomeIcon icon="user" className="mr-3" />
                    Profile
                  </Link>
                  <Link
                    to={path.settings}
                  >
                    <FontAwesomeIcon icon={faGear} className="mr-3" />
                    Settings
                  </Link>
                  <Link
                    to="#"
                    id="qsLogoutBtn"
                    onClick={() => logoutWithRedirect()}
                  >
                    <FontAwesomeIcon icon="power-off" className="mr-3" />
                    Log out
                  </Link>
              </Nav>
            )}
          </Collapse>
        </Container>
      </Navbar>
    </div>
    
  );
};

export default NavBar;

/**
 * 
 * 
 <NavItem>
                  <NavLink
                    tag={RouterNavLink}
                    to="/list/users"
                    exact
                    activeClassName="router-link-exact-active"
                  >
                    Listar Utilizadores
                  </NavLink>
                </NavItem>
 */