import React from 'react';
import { Footer } from './components/layout'
import {Login, CreateAccount} from './components/util';
import { Homepage } from './components/homepage';
import './App.css';
import './components/homepage/homepage.css';
import {Provider} from "react-redux";
import {store} from "./store";
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import {Household, MedicalHistory, Profile} from "./components/profile";
import {AppointmentPage, AppointmentsPage, FormAppointmentPage} from "./components/appointments";

function HomepageApp() {

    //npm install --save @fortawesome/fontawesome-svg-core @fortawesome/free-solid-svg-icons @fortawesome/react-fontawesome

    return (
        <div>
            <div><Homepage/></div>
            <div><Footer/></div>
        </div>
    );

}

function ProfileApp() {
    return (
        <div>
            <div><Profile/></div>
            <div><Footer/></div>
        </div>
    );
}

function HouseholdApp() {
    return (
        <div>
            <div><Household/></div>
            <div><Footer/></div>
        </div>
    );
}

function MedicalHistoryApp() {
    return (
        <div>
            <div><MedicalHistory/></div>
            <div><Footer/></div>
        </div>
    )
}

function AppointmentsApp() {
    return (
        <div>
            <div><AppointmentsPage/></div>
            <div><Footer/></div>
        </div>
    )
}

function AppointmentApp() {
    return (
        <div>
            <div><AppointmentPage/></div>
            <div><Footer/></div>
        </div>
    )
}

function ScheduleApp() {
    return (
        <div>
            <div><FormAppointmentPage/></div>
            <div><Footer/></div>
        </div>
    );
}

function LoginPage() {

    return (
        <div style = {{position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)'}}>
            <Login/>
        </div>
    )

}

function CreatePage() {

    return (
        <div style = {{position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)'}}>
            <CreateAccount/>
        </div>
    );

}

const RdxApp = () => (
    <Provider store={store}>
        <Router>
            <Routes>
                <Route path='/' element={<HomepageApp />} />
                <Route path='/login' element={<LoginPage />} />
                <Route path='/create-account' element={<CreatePage />} />
                <Route path='/profile' element={<ProfileApp />} />
                <Route path='/profile/household' element={<HouseholdApp />} />
                <Route path='/profile/medical-history' element={<MedicalHistoryApp />} />
                <Route path='/appointments' element={<AppointmentsApp />} />
                <Route path='/appointments/appointment-id' element={<AppointmentApp />} />
                <Route path='/appointments/schedule' element={<ScheduleApp />} />
            </Routes>
        </Router>
    </Provider>
);


export default RdxApp;
