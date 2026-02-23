import { RouterProvider } from "react-router-dom";
import { router } from "./routes/router";
import "./styles/global.css";

function App() {
  return (
    <>
      <RouterProvider router={router} />
      <div style={{ position: "fixed", bottom: 10, right: 10, opacity: 0.5 }}>
        App Loaded
      </div>
    </>
  );
}

export default App;
