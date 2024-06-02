import React, {useState} from "react";
import {Link} from "react-router-dom";
import {MyClinicHeader} from "../homepage";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft} from "@fortawesome/free-solid-svg-icons";
import PrescriptionsTable, {ProfileCard} from "../profile";

export const ClinicalStaff = () => {

    const [specialityFilter, setStateFilter] = useState('All')
    const [doctorSearch, setDoctorSearch] = useState("")

    const handleSpecialityChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setStateFilter(event.target.value);
    }

    const handleDoctorSearch = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setDoctorSearch(event.target.value);
    }

    const exampleStaff = [
        { medicalSpeciality: 'cardiology', name: 'Dr. Smith', email: 'smith@mail.com' },
        { medicalSpeciality: 'orthopedics', name: 'Dr. Johnson', email: 'johnson@mail.com' },
        { medicalSpeciality: 'ophthalmology', name: 'Dr. Brown', email: 'brown@mail.com' },
        { medicalSpeciality: 'gynecology obstetrics', name: 'Dr. Patel', email: 'patel@mail.com' },
    ]

    const makeAppointment = () => {

    }

    const filteredStaff = exampleStaff.filter(staff => {
        if (specialityFilter !== 'All' && staff.medicalSpeciality !== specialityFilter) {
            return false;
        }
        if (doctorSearch !== '' && !staff.name.includes(doctorSearch)) {
            return false;
        }
        return true;
    })

    const clinicalStaff =
        <div>
            <div className={"filters"}>
                <label>
                    Speciality:&nbsp;
                    <select value={specialityFilter} onChange={handleSpecialityChange}>
                        <option value="All">All</option>
                        <option value="general practice family medicine">General practice family medicine</option>
                        <option value="pediatrics">Pediatrics</option>
                        <option value="gynecology obstetrics">Gynecology obstetrics</option>
                        <option value="cardiology">Cardiology</option>
                        <option value="orthopedics">Orthopedics</option>
                        <option value="dermatology">Dermatology</option>
                        <option value="psychiatry">Psychiatry</option>
                        <option value="ophthalmology">Ophthalmology</option>
                        <option value="otolaryngologies">Otolaryngologies</option>
                        <option value="gastroenterology">Gastroenterology</option>
                    </select>
                </label>
                <label>
                    Search Doctor:&nbsp;
                    <input type="text" value={doctorSearch} onChange={handleDoctorSearch}
                           className="doctor-search-input"/>
                </label>
            </div>
            <table className="prescriptions-table">
                <thead>
                <tr>
                    <th>Doctor</th>
                    <th>Speciality</th>
                    <th>Email</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {filteredStaff.map((staff, index) => (
                    <tr key={index}>
                        <td>{staff.name}</td>
                        <td>{staff.medicalSpeciality}</td>
                        <td>{staff.email}</td>
                        <td>
                            <Link to="/appointments/schedule">
                                <button className="open-button">
                                    Make appointment
                                </button>
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>

    return clinicalStaff
}

export const ClinicalStaffPage = () => {

const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    const exampleStaff = [
        { medicalSpeciality: 'cardiology', name: 'Dr. Smith', email: 'smith@mail.com' },
        { medicalSpeciality: 'gynecology obstetrics', name: 'Dr. Patel', email: 'patel@mail.com' },
    ]

return (
    <div className={"profile-page"}>
        <div className={"left"}>
            <MyClinicHeader />
            <ProfileCard name={patient.name} />
        </div>

        <div className={"right"}>
            <div>
                <div className={"page-title"}>
                    <p>Recommended doctors</p>
                </div>
                    <table className="prescriptions-table">
                        <thead>
                        <tr>
                            <th>Doctor</th>
                            <th>Speciality</th>
                            <th>Email</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {exampleStaff.map((staff, index) => (
                            <tr key={index}>
                                <td>{staff.name}</td>
                                <td>{staff.medicalSpeciality}</td>
                                <td>{staff.email}</td>
                                <td>
                                    <Link to="/appointments/schedule">
                                        <button className="open-button">
                                            Make appointment
                                        </button>
                                    </Link>
                                </td>
                            </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <Link to="/profile">
                    <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                </Link>
            </div>
        </div>
)
}