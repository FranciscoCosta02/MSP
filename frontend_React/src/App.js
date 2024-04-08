import React,{useState} from "react";
import { Route, Switch} from "react-router-dom";
import { Container } from "reactstrap";
import NavBar from "./components/menu/NavBar";
//import Footer from "./components/rodape/Footer";
import Home from "./views/pages/homePage/Home";
//import Page404 from "./views/error/Page404";
import HomeAuth from "./views/pages/homePage/HomeAuth";
import AnomaliesToSolve from "./views/pages/anomalies/AnomaliesToResolve";
import AnomaliesSolved from "./views/pages/anomalies/AnomaliesResolved";
//import CreateActivity from "./views/pages/activities/createActivity";
import Settings from "./views/pages/settings/Settings";
import ListActivities from "./views/pages/activities/ListActivities";
import Notifications from "./views/pages/notifications/Notifications";
import MyNotifications from "./views/pages/notifications/MyNotifications";

import Login from "./views/auth/login/Login";
import Photos from "./views/pages/photos/Photos";
import EstatisticaUsers from "./views/statistics/EstatisticaUsers";
import Cookies from 'js-cookie';
import Profile from './views/pages/profile/ProfilePage';

// styles
import "./App.css";
import Fab from '@mui/material/Fab';
import BugReportIcon from '@mui/icons-material/BugReport';
// fontawesome
import initFontAwesome from "./utils/initFontAwesome";
import ListUsers from "./views/pages/listUsers/ListUsers";
import ResetPassword from "./views/auth/resetPassword/ResetPassword";
import FAQs from "./views/pages/faqs/FAQs";
import Groups from "./views/pages/groups/Groups";
import { path } from "./services/var/var";
import MyRooms from "./views/pages/rooms/MyRooms";
import { BugModal } from "./components/pages/bug/BugModal";
import BugList from "./views/pages/bug/BugList";
import Registration from "./views/auth/register/Registration";
initFontAwesome(); 

const fabStyle = {
  position: 'fixed',
  bottom: 16,
  right: 16,
};

const App = () => {

  const [openBugModal, setOpenBugModal] = useState(false);
  const isAuthenticated =Cookies.get("loginToken");


  return (
      <div id="app" className="d-flex flex-column h-100">
        <NavBar/>
        <Container className="flex-grow-1 mt-5">
          <Switch>
            {
              isAuthenticated?
              <>
                <Route path={path.home} exact component={HomeAuth} />
                <Route path={path.listUsers} exact component={ListUsers} />
                <Route path={path.stats} exact component={EstatisticaUsers} />
                <Route path={path.profile} exact component={Profile} />
                <Route path={path.toSolve} exact component={AnomaliesToSolve} />
                <Route path={path.solved} exact component={AnomaliesSolved} />
                <Route path={path.listActivities} exact component={ListActivities} />
                <Route path={path.notifications} exact component={Notifications} />
                <Route path={path.photos+"/:activitiID/:actTitle"} exact component={Photos} />
                <Route path={path.faqs} exact component={FAQs} />
                <Route path={path.groups} exact component={Groups} />
                <Route path={path.myNotifications} exact component={MyNotifications} />
                <Route path={path.settings} exact component={Settings} />
                <Route path={path.myRooms} exact component={MyRooms} />
                <Route path={path.register} exact component={Registration} />
                <Route path={path.bugList} exact component={BugList} />
                <Fab color="primary" aria-label="add" sx={fabStyle} onClick={() => setOpenBugModal(true)}>
                  <BugReportIcon />
                </Fab>
                <BugModal
                  open={openBugModal}
                  onClose={() => setOpenBugModal(false)}
                />
              </>
              :
              <>
                <Route path={path.home} exact component={Home} />
                <Route path={path.login}  exact component={Login} />
                <Route path={path.recoverPWD}  exact component={ResetPassword} />
              </>             
            }
            
          </Switch>
        </Container>
        
      </div>
  );
};

export default App;

//<Footer/>