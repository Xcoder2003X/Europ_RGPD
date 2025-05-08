import React, { useEffect, useState } from "react";
import "./Robot3D.css"; // Import CSS for animation

const Robot3D = ({ isExcited, robotImage }) => {
  const [rotate, setRotate] = useState(false);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    if (isExcited) {
      setRotate(true);
      const timeout = setTimeout(() => setRotate(false), 1000); // Reset after 1 second
      return () => clearTimeout(timeout);
    }
  }, [isExcited]);

  return (
    <div className={`robot-container ${rotate ? "rotate" : ""}`}>
      <img
        src={robotImage} // Dynamically set the robot image
        alt="Robot"
        className={`robot-image ${loaded ? 'loaded' : 'loading'}`}
        loading="lazy"
        onLoad={() => setLoaded(true)}
        style={{
          
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
        }}
        
      />
    </div>
  );
};

export default Robot3D;