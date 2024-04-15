import {CSSProperties, ReactNode} from 'react'
import './layout.css';
import {Link} from "react-router-dom";

interface BannerProps {
    title: string
}
export const Banner = ({ title }: BannerProps) => <div style={{ fontWeight: 'bold' , textAlign: 'center' }}>{ title }</div>

const headerStyle: CSSProperties = {
    backgroundColor: '#0f3c85',
    color: '#ffffff',
    textAlign: 'center',
    padding: '20px',
};

export const Header = (props: { children: React.ReactNode }) => (
    <header style={headerStyle}>
        <Banner title="MyClinic" />
        {props.children}
    </header>
);


export const Container = (props: { children?: ReactNode }) => <div>{ props.children }</div>
export const Footer = () =>
    <footer className="footer">
        <ul className="menu">
            <li className="menu__item"><Link className="menu__link" to="/">Home</Link></li>
            <li className="menu__item"><Link className="menu__link" to="/profile">Profile</Link></li>
            <li className="menu__item"><Link className="menu__link" to="/appointments">Appointments</Link></li>
        </ul>
        <p>2024 | MSP</p>
    </footer>
