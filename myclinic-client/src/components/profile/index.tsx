import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft, faSignOutAlt} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import './profile.css';
import avatar from "./avatar.png";
import {Link, useNavigate} from "react-router-dom";
import {MyClinicHeader} from "../homepage";

export const ProfileButton = () => {
    const navigate = useNavigate();

    const handleProfileClick = () => {
        navigate('/profile');
    };

    return (
        <button className="simple-button" onClick={handleProfileClick}>Profile</button>
    );
}

export const ProfileCard = (patient: { name: string | number | boolean | React.ReactElement<any, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined; }) => {
    return (
        <div>
            <div className={"card-profile"}>
                <div className="profile-text-card">
                    <img src={avatar} alt="Profile" className={"profile-image-card"} style={{marginRight: "5%"}}/>
                    <p className="name">{patient.name}</p>
                    <ProfileButton/>
                    <LogoutButton/>
                </div>
            </div>
        </div>
    )
}

// @ts-ignore
const HouseholdPersonCard = ({ name, birthDate }) => (
    <div className="card-household">
        <p className={"page-sub-title"}>{name}</p>
        <p className={"page-text-info"}>{birthDate}</p>
    </div>
);

export const LogoutButton = () => {
    const navigate = useNavigate();

    const handleProfileClick = () => {
        navigate('/login');
    };

    return (
        <button className="light-button" onClick={handleProfileClick} style={{marginTop: "5%"}}><FontAwesomeIcon icon={faSignOutAlt} /> Logout</button>
    );
}

const PrescriptionsTable = () => {
    const examplePrescriptions = [
        { date: '2024-04-01', doctor: 'Dr. Smith', content: '/data/prescription1.pdf' },
        { date: '2024-04-05', doctor: 'Dr. Johnson', content: '/data/prescription1.pdf' },
        { date: '2024-04-10', doctor: 'Dr. Brown', content: '/data/prescription1.pdf' },
        { date: '2024-04-15', doctor: 'Dr. Patel', content: '/data/prescription1.pdf' },
    ]

    const handleOpenPrescription = (content: string | URL | undefined) => {
        window.open(content, '_blank');
    }


    return (
        <table className="prescriptions-table">
            <thead>
            <tr>
                <th>Date</th>
                <th>Doctor</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            {examplePrescriptions.map((prescription, index) => (
                <tr key={index}>
                    <td>{prescription.date}</td>
                    <td>{prescription.doctor}</td>
                    <td>
                        <button className="open-button" onClick={() => handleOpenPrescription(prescription.content)}>
                            Open prescription
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    )
}

export default PrescriptionsTable;


export const Profile = () => {

    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    const profile =
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader/>
                <ProfileCard name={patient.name}/>
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Profile</p>
                    </div>
                    <div className={"profile-container"}>
                        <img src={avatar} alt="Profile" className={"profile-image"} style={{marginRight: "5%"}}/>
                        <div>
                            <p className={"page-sub-title"}>Name</p>
                            <p className={"page-text-info"}>Rita Lopes</p>
                            <p className={"page-sub-title"}>Email</p>
                            <p className={"page-text-info"}>rg.lopes@campus.fct.unl.pt</p>
                            <p className={"page-sub-title"}>Birth date</p>
                            <p className={"page-text-info"}>20/12/2001</p>
                            <p className={"page-sub-title"}>Phone number</p>
                            <p className={"page-text-info"}>939283352</p>
                            <p className={"page-sub-title"}>NIF</p>
                            <p className={"page-text-info"}>241975417</p>
                        </div>
                    </div>
                    <div>
                        <p className={"page-sub-title"}>Prescriptions</p>
                        <PrescriptionsTable/>
                    </div>

                    <p className={"page-sub-title"}>Medical History</p>
                    <Link to="/profile/medical-history">
                            <button className={"simple-button"}>See medical history</button>
                    </Link>
                    <p className={"page-sub-title"}>Household</p>
                    <Link to="/profile/household">
                        <button className={"simple-button"}>See household</button>
                    </Link>
                    <p></p>
                    <Link to="/">
                        <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                    </Link>
                </div>
            </div>
        </div>

    return profile
}

export const MedicalHistory = () => {
    const exampleAppointments = [
        { date: '2024-04-17', regime: 'in person', type: 'appointment', state: 'completed', doctor: 'Dr. Smith' },
        { date: '2024-04-20', regime: 'online', type: 'appointment', state: 'completed', doctor: 'Dr. Johnson' },
        { date: '2024-05-10', regime: 'in person', type: 'appointment', state: 'completed', doctor: 'Dr. Brown' },
        { date: '2024-05-15', regime: 'online', type: 'appointment', state: 'completed', doctor: 'Dr. Patel' },
    ]

    const exampleExams = [
        { date: '2024-04-17', regime: 'in person', type: 'exam', state: 'completed', doctor: 'Dr. Smith' },
        { date: '2024-04-20', regime: 'online', type: 'exam', state: 'completed', doctor: 'Dr. Johnson' },
        { date: '2024-05-10', regime: 'in person', type: 'exam', state: 'completed', doctor: 'Dr. Brown' },
        { date: '2024-05-15', regime: 'online', type: 'exam', state: 'completed', doctor: 'Dr. Patel' },
    ]

    const handleOpenExam = () => {

    }

    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    return (
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader />
                <ProfileCard name={patient.name} />
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Medical History</p>
                    </div>
                    <p className={"page-sub-title"}>Prescriptions</p>
                    <PrescriptionsTable/>
                    <p className={"page-sub-title"}>Appointments</p>
                    <table className="prescriptions-table">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Regime</th>
                            <th>Type</th>
                            <th>State</th>
                            <th>Doctor</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {exampleAppointments.map((appointment, index) => (
                            <tr key={index}>
                                <td>{appointment.date}</td>
                                <td>{appointment.regime}</td>
                                <td>{appointment.type}</td>
                                <td>{appointment.state}</td>
                                <td>{appointment.doctor}</td>
                                <td>
                                    <Link to="/appointments/appointment-id">
                                        <button className="open-button">
                                            Open appointment
                                        </button>
                                    </Link>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    <p className={"page-sub-title"}>Exams</p>
                    <table className="prescriptions-table">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Regime</th>
                            <th>Type</th>
                            <th>State</th>
                            <th>Doctor</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {exampleExams.map((appointment, index) => (
                            <tr key={index}>
                                <td>{appointment.date}</td>
                                <td>{appointment.regime}</td>
                                <td>{appointment.type}</td>
                                <td>{appointment.state}</td>
                                <td>{appointment.doctor}</td>
                                <td>
                                    <Link to="/appointments/appointment-id">
                                        <button className="open-button">
                                            Open appointment
                                        </button>
                                    </Link>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    <Link to="/profile">
                        <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export const Household = () => {

    const patient = {
        name: 'Pepper Potts',
        email: 'teste@mail.com',
        birthDate: '20/12/2001',
        phone: '999999999',
        nif: '111111111',
        household: [
            { name: 'Tony Potts', birthDate: '12/09/1967' },
            { name: 'Morgan Potts', birthDate: '25/03/1999' },
            { name: 'Happy Potts', birthDate: '21/08/2002' },
            { name: 'James Potts', birthDate: '14/01/1972' }
        ]
    }

    const renderHouseholdCards = () => {
        return patient.household.map((person, index) => (
            <div className="household-row" key={index}>
                <HouseholdPersonCard name={person.name} birthDate={person.birthDate} />
            </div>
        ));
    };

    return (
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader />
                <ProfileCard name={patient.name} />
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Household</p>
                    </div>
                    {renderHouseholdCards()}
                    <Link to="/profile">
                        <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                    </Link>
                </div>
            </div>
        </div>
    )
}