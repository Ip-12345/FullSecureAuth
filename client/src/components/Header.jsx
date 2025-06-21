import React, { useContext } from 'react'
import { assets } from '../assets/assets'
import { AppContext } from '../context/AppContext'

function Header() {

  const {userData}=useContext(AppContext);

  return (
    <div className='text-center d-flex flex-column align-items-center justify-content-center py-5 px-3' style={{minHeight:"80vh"}}>
      <img src={assets.header} alt='header' width={120} className='mb-4'/>
      <h5 className='fw-semibold'>Hey {userData? userData.name:'Developer'}<span role='img' area-label='wave'> 👋</span></h5>
      <h1 className='fw-bold display-5 mb-3'>Welcome to induopia</h1>
      <p className='text-muted mb-4 fs-5' style={{maxWidth:"500px"}}>Let's start with a quick tour and you can set up the authentication in no time!</p>
      <button className='btn btn-outline-dark rounded-pill px-4 py-2'>Get Started</button>
    </div>
  )
}

export default Header
