import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faArrowLeft,
    faEye,
    faFileWaveform,
    faHospital,
    faLaptopMedical,
} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import './appointments.css';
import {Link} from "react-router-dom";
import {ProfileCard} from "../profile";
import {MyClinicHeader} from "../homepage";

export const Appointments = () => {
    return <div>
        <UpcomingAppointmentsTable/>
        <p></p>
        <Link to="/appointments">
            <button className={"light-button"}><FontAwesomeIcon icon={faEye}/> See more</button>
        </Link>
    </div>
}

export const UpcomingAppointments = () => {
    return <div className={"card-schedule"}>
        <Link to="/appointments/schedule">
            <button className={"appointment-button"}>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<FontAwesomeIcon icon={faHospital}/> Consultations&nbsp;&nbsp;&nbsp;&nbsp;
            </button>
        </Link>
        <Link to="/appointments/schedule">
            <button className={"appointment-button"}>
                <FontAwesomeIcon icon={faLaptopMedical}/> Videoconsultations
            </button>
        </Link>
        <Link to="/appointments/schedule">
            <button className={"appointment-button"}>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<FontAwesomeIcon icon={faFileWaveform}/> Exams&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </button>
        </Link>
    </div>
}

const UpcomingAppointmentsTable = () => {
    const exampleAppointments = [
        { date: '2024-04-17', regime: 'in person', type: 'appointment', state: 'scheduled', doctor: 'Dr. Smith' },
        { date: '2024-04-20', regime: 'online', type: 'appointment', state: 'scheduled', doctor: 'Dr. Johnson' },
        { date: '2024-05-10', regime: 'in person', type: 'exam', state: 'scheduled', doctor: 'Dr. Brown' },
        { date: '2024-05-15', regime: 'online', type: 'appointment', state: 'scheduled', doctor: 'Dr. Patel' },
    ]

    const handleOpenAppointment = () => {

    }

    return (
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
    )
}

// @ts-ignore
const UpcomingAppointmentsTableFilter = ({ regimeFilter, typeFilter, stateFilter }) => {
    const exampleAppointments = [
        { date: '2024-04-17', regime: 'in person', type: 'appointment', state: 'scheduled', doctor: 'Dr. Smith' },
        { date: '2024-04-20', regime: 'online', type: 'appointment', state: 'scheduled', doctor: 'Dr. Johnson' },
        { date: '2024-05-10', regime: 'in person', type: 'exam', state: 'scheduled', doctor: 'Dr. Brown' },
        { date: '2024-05-15', regime: 'online', type: 'appointment', state: 'scheduled', doctor: 'Dr. Patel' },
    ]

    const handleOpenAppointment = () => {

    }

    const filteredAppointments = exampleAppointments.filter(appointment => {
        if (regimeFilter !== 'All' && appointment.regime !== regimeFilter) {
            return false;
        }
        if (typeFilter !== 'All' && appointment.type !== typeFilter) {
            return false;
        }
        if (stateFilter !== 'All' && appointment.state !== stateFilter) {
            return false;
        }
        return true;
    })

    return (
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
            {filteredAppointments.map((appointment, index) => (
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
    )
}


export const AppointmentsPage = () => {

    const [regimeFilter, setRegimeFilter] = useState('All')
    const [typeFilter, setTypeFilter] = useState('All')
    const [stateFilter, setStateFilter] = useState('All')
    const [doctorSearch, setDoctorSearch] = useState("")
    const [sortBy, setSortBy] = useState("dateAsc")

    const handleRegimeChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setRegimeFilter(event.target.value);
    }

    const handleTypeChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setTypeFilter(event.target.value);
    }

    const handleStateChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setStateFilter(event.target.value);
    }

    const handleDoctorSearch = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setDoctorSearch(event.target.value);
    }

    const handleSortByChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setSortBy(event.target.value);
    }

    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    return (
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader/>
                <ProfileCard name={patient.name}/>
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Appointments</p>
                    </div>
                    <div className={"filters"}>
                        <label>
                            Regime:&nbsp;
                            <select value={regimeFilter} onChange={handleRegimeChange}>
                                <option value="All">All</option>
                                <option value="in person">In Person</option>
                                <option value="online">Online</option>
                            </select>
                        </label>
                        <label>
                            Type:&nbsp;
                            <select value={typeFilter} onChange={handleTypeChange}>
                                <option value="All">All</option>
                                <option value="appointment">Appointment</option>
                                <option value="exam">Exam</option>
                            </select>
                        </label>
                        <label>
                            State:&nbsp;
                            <select value={stateFilter} onChange={handleStateChange}>
                                <option value="All">All</option>
                                <option value="scheduled">Scheduled</option>
                                <option value="completed">Completed</option>
                                <option value="cancelled">Cancelled</option>
                            </select>
                        </label>
                        <label>
                            Search Doctor:&nbsp;
                            <input type="text" value={doctorSearch} onChange={handleDoctorSearch} className="doctor-search-input"/>
                        </label>
                        <label>
                            Sort By:&nbsp;
                            <select value={sortBy} onChange={handleSortByChange}>
                                <option value="dateAsc">Date (Ascending)</option>
                                <option value="dateDesc">Date (Descending)</option>
                                <option value="doctorAsc">Doctor (A-Z)</option>
                                <option value="doctorDesc">Doctor (Z-A)</option>
                            </select>
                        </label>
                    </div>
                    <p className={"page-sub-title"}>Scheduled</p>
                    <UpcomingAppointmentsTableFilter
                        regimeFilter={regimeFilter}
                        typeFilter={typeFilter}
                        stateFilter={stateFilter}
                    />
                    <p></p>
                    <Link to="/">
                        <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export const AppointmentPage = () => {

    const exampleAppointment = {date: '2024-04-15 20:20:00', regime: 'In person', type: 'Appointment', state: 'Scheduled', doctor: 'Dr. Smith'};
    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    const canCheckin = () => {
        const currentDate = new Date()
        const date = new Date(exampleAppointment.date)

        return exampleAppointment.state === 'Scheduled' &&
            currentDate.getDate() <= date.getDate() &&
            currentDate.getDay() === date.getDay()
    }

    const hasPrescription = () => {
        //ver se appointment tem prescription

        return exampleAppointment.state === 'Completed'
    };

    return <div className={"profile-page"}>
        <div className={"left"}>
            <MyClinicHeader/>
            <ProfileCard name={patient.name}/>
        </div>

        <div className={"right"}>
            <div>
                <div className={"page-title"}>
                    <p>Appointment</p>
                </div>

                <p className={"page-sub-title"}>Date</p>
                <p className={"page-text-info"}>{exampleAppointment.date}</p>
                <p className={"page-sub-title"}>Doctor</p>
                <p className={"page-text-info"}>{exampleAppointment.doctor}</p>
                <p className={"page-sub-title"}>Regime</p>
                <p className={"page-text-info"}>{exampleAppointment.regime}</p>
                <p className={"page-sub-title"}>Type</p>
                <p className={"page-text-info"}>{exampleAppointment.type}</p>
                <p className={"page-sub-title"}>State</p>
                <p className={"page-text-info"}>{exampleAppointment.state}</p>
                {hasPrescription() && (
                    <div>
                        <p className={"page-sub-title"}>Prescription</p>
                        <button className={"simple-button"}>See prescription</button>
                    </div>
                )}
                {canCheckin() && (
                    <div>
                        <p className={"page-sub-title"}>Check-in</p>
                        <button className={"simple-button"}>Check-in</button>
                    </div>
                )}
                <p></p>
                <Link to="/">
                    <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft}/> Back</button>
                </Link>
            </div>
        </div>
    </div>
}

// @ts-ignore
const FormAppointment = ({type: info}) => {
    const doctors = [{name: "Dr. Smith"}, {name: "Dr. Johnson"}, {name: "Dr. Brown"}, {name: "Dr. Patel"}]
    const [formData, setFormData] = useState({
        date: '',
        regime: '',
        type: '',
        doctor: '',
    })

    const handleChange = (e: { target: { name: any; value: any } }) => {
        const { name, value } = e.target
        setFormData({
            ...formData,
            [name]: value
        })
    }

    useEffect(() => {
        if (info === 'online') {
            setFormData({
                ...formData,
                regime: 'Online',
                type: 'Appointment'
            })
        } else {
            if (info === 'exam') {
                setFormData({
                    ...formData,
                    regime: 'In Person',
                    type: 'Exam'
                })
            } else {
                setFormData({
                    ...formData,
                    regime: 'In Person',
                    type: 'Appointment'
                })
            }
        }
    }, [info])

    const handleSubmit = (e: { preventDefault: () => void }) => {
        e.preventDefault()
        //mandar dados para o backend
        console.log(formData);
    }

    return (
        <form onSubmit={handleSubmit}>
            <p><label className={"form-label"}>
                Date:&nbsp;
                <input type="date" name="date" value={formData.date} onChange={handleChange} className={"form-input"}/>
            </label></p>
            <p><label className={"form-label"}>
                Regime:&nbsp;
                <input type="text" name="regime" value={formData.regime} readOnly className={"form-input-locked"}/>
            </label></p>
            <p><label className={"form-label"}>
                Type:&nbsp;
                <input type="text" name="type" value={formData.type} readOnly className={"form-input-locked"}/>
            </label></p>
            <p><label className={"form-label"}>
                Doctor:&nbsp;
                <select name="doctor" value={formData.doctor} onChange={handleChange} className={"form-input"}>
                    <option value="">Select doctor</option>
                    {doctors.map((doctor, index) => (
                        <option key={index} value={doctor.name}>{doctor.name}</option>
                    ))}
                </select>
            </label></p>
            <button type="submit" className={"simple-button"}>Submit</button>
            <p></p>
            <Link to="/">
                <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft}/> Back</button>
            </Link>
        </form>
    )
}

export const FormAppointmentPage = () => {
    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    return (
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader/>
                <ProfileCard name={patient.name}/>
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Schedule appointment</p>
                    </div>
                    <div><FormAppointment type={""}/></div>
                </div>
            </div>
        </div>
    )
}