import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate, Outlet, useLocation } from 'react-router-dom';

const PrivateRoute = ({ publicPage = false, adminOnly = false }) => {
    const { user } = useSelector((state) => state.auth);
    const isAdmin = user && user?.roles?.includes("ROLE_ADMIN");
    const isSeller = user && user?.roles.includes("ROLE_SELLER");
    const location = useLocation();

    // 1. Handle public pages (login, register)
    if (publicPage) {
        return user ? <Navigate to="/" /> : <Outlet />
    }

    // 2. If user is not logged in at all, send them to login
    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // 3. Handle adminOnly routes
    if (adminOnly) {
        if (!isAdmin && !isSeller) {
            return <Navigate to="/" replace />;
        }
        if (isSeller && !isAdmin) {
            const sellerAllowedPaths = ["/admin/orders", "/admin/products"];
            const sellerAllowed = sellerAllowedPaths.some(path => 
                location.pathname.startsWith(path)
            );
            if (!sellerAllowed) {
                return <Navigate to="/" replace />;
            }
        }
    }

    // 4. For regular user routes (like /checkout), any authenticated user can pass through
    return <Outlet />;
}

export default PrivateRoute;