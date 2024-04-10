import React from 'react';
import { Header, Footer } from './components/layout'
import { Navigation, Login } from './components/util';
import { Homepage } from './components/homepage';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStethoscope, faLaptopMedical, faFileWaveform, faHospital } from '@fortawesome/free-solid-svg-icons';
import './App.css';
import './components/homepage/homepage.css';
import {Provider} from "react-redux";
import {store} from "./store";
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';

function HomepageApp() {

    //npm install --save @fortawesome/fontawesome-svg-core @fortawesome/free-solid-svg-icons @fortawesome/react-fontawesome

    return (
        <div><Homepage/></div>
    );

}

const RdxApp = () => (
    <Provider store={store}>
        <Router>
            <Routes>
                <Route path='/' element={<HomepageApp />} />
            </Routes>
        </Router>
    </Provider>
);


export default RdxApp;
