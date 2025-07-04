import React, { useContext, useEffect, useRef, useState } from 'react'
import { assets } from '../assets/assets'
import { useNavigate } from 'react-router-dom'
import { AppContext } from '../context/AppContext';
import axios from 'axios';
import { toast } from 'react-toastify';
import AdminDashboard from './AdminDashboard';

function Menubar() {
  const navigate=useNavigate();
  const {userData, backendURL, setIsLoggedIn, setUserData, userRole}=useContext(AppContext);
  const[dropDownOpen, setDropDownOpen]=useState(false);
  const dropDownRef=useRef(null);

  useEffect(()=>{
    const handleClickOutside=()=>{
      if(dropDownRef.current && !dropDownRef.current.contains(event.target)){
        setDropDownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return ()=>document.removeEventListener('mousedown', handleClickOutside);
  },[]);

  const handleLogout=async()=>{
    try{
      axios.defaults.withCredentials=true; 
      const response=await axios.post(backendURL+'/logout');
      if(response.status==200){
        setIsLoggedIn(false);
        setUserData(false);
        navigate('/');
      }
    }
    catch(error){
      toast.error(error.response.data.message);
    }
  }

  const sendVerificationOtp=async()=>{
    try{
      axios.defaults.withCredentials=true;
      const response=await axios.post(backendURL+'/send-otp');
      if(response.status===200){
        navigate('/email-verify');
        toast.success('OTP has been sent successfully.')
      }
      else{
        toast.error('Unable to send OTP.')
      }
    }
    catch(error){
      toast.error(error.response.data.message);
    }
  }

  return (
    <nav className='navbar bg-white px-5 py-4 d-flex justify-content-between align-items-center'>
      <div className='d-flex align-items-center gap-2 '>
        <img src={assets.logohome} alt='logo' width={32} height={32}/>
        <div className='d-flex justify-content-between align-items-center w-100 gap-4'>
          <div className='fw-bold fs-4 text-dark'>IPVault</div>
          <div className='fs-6 text-dark'>An Auth – By Indupriya Bejugam</div>
        </div>

        {/* <span className='fw-bold fs-4 text-dark'>IPVault</span>
        <p className='fs-7 text-dark'>An Auth -By Indupriya Bejugam</p> */}
      </div>

      {userData?(
        <div className="position-relative" ref={dropDownRef}>
          <div className="bg-dark text-white rounded-circle d-flex justify-content-center align-items-center"
            style={{width:'40px', height:'40px',cursor:'pointer', userSelect:'none'}}
            onClick={()=>setDropDownOpen((prev)=> !prev)}
          >
            {userData.name[0].toUpperCase()}
          </div>

          {dropDownOpen && (<div className="position-absolute shadow bg-white rounded p-2" style={{top:'50px', right:0, zIndex:100}}>
            {!userData.isAccountVerified && (
              <div className="dropdown-item py-1 px-2" style={{cursor:'pointer'}} onClick={sendVerificationOtp}>
                Verify email
              </div>
            )}

            {userRole === 'ADMIN' && (
              <AdminDashboard/>
            )}

            <div className="dropdown-item py-1 px-2 text-danger" style={{cursor:'pointer'}} onClick={handleLogout}>
              Logout
            </div>
          </div>) }

        </div>
      ):(
        <div className='btn btn-outline-dark rounded-pill px-3' onClick={()=>navigate("/login")}>
          Login
          <i className='bi bi-arrow-right ms-2'></i>
        </div>
      )}
    </nav>
  )
}

export default Menubar