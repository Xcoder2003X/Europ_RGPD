import React, { useEffect, useState } from "react";
import "./Robot3D.css"; // Import CSS for animation

const Robot3D = ({ isExcited, robotImage }) => {
  const [rotate, setRotate] = useState(false);

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
        className="robot-image"
      />
    </div>
  );
};

export default Robot3D;