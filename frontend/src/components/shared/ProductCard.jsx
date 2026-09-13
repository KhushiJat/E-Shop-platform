import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import ProductViewModal from "./ProductViewModal";
import truncateText from "../../utils/truncateText";
import { useDispatch } from "react-redux";
import { addToCart } from "../../store/actions";
import toast from "react-hot-toast";
import { formatPrice } from "../../utils/formatPrice"; // Adjust path if your utils folder is located elsewhere

const ProductCard = ({
        productId,
        productName,
        image,
        description,
        quantity,
        price,
        discount,
        specialPrice,
        about = false,
}) => {
    const [openProductViewModal, setOpenProductViewModal] = useState(false);
    const btnLoader = false;
    const [selectedViewProduct, setSelectedViewProduct] = useState("");
    const isAvailable = quantity && Number(quantity) > 0;
    const dispatch = useDispatch();

    const handleProductView = (product) => {
        if (!about) {
            setSelectedViewProduct(product);
            setOpenProductViewModal(true);
        }
    };

    const addToCartHandler = (cartItems) => {
        dispatch(addToCart(cartItems, 1, toast));
    };

    return (
        <div className="rounded-lg shadow-xl overflow-hidden transition-shadow duration-300">
            <div 
                onClick={() => {
                    handleProductView({
                        id: productId,
                        productName,
                        image,
                        description,
                        quantity,
                        price,
                        discount,
                        specialPrice,
                    });
                }} 
                className="w-full overflow-hidden aspect-3/2 flex justify-center items-center bg-gray-50 cursor-pointer"
            >
                <img 
                    className="max-h-full max-w-full object-contain transition-transform duration-300 transform hover:scale-105"
                    src={image} 
                    alt={productName} 
                />
            </div>

            <div className="p-4">
                <h2 
                    onClick={() => {
                        handleProductView({
                            id: productId,
                            productName,
                            image,
                            description,
                            quantity,
                            price,
                            discount,
                            specialPrice,
                        });
                    }}
                    className="text-lg font-semibold mb-2 cursor-pointer"
                >
                    {truncateText(productName, 50)}
                </h2>
                
                <div className="min-h-20 max-h-20">
                    <p className="text-gray-600 text-sm">
                        {truncateText(description, 80)}
                    </p>
                </div>

                { !about && (
                    <div className="flex items-center justify-between mt-4">
                        {specialPrice ? (
                           <div className="flex flex-col">
                           <span className="text-gray-400 line-through">
                             {formatPrice(price, true)} {/* Added true for USD conversion */}
                            </span>

                            <span className="text-xl font-bold text-slate-700">
                              {formatPrice(specialPrice, true)} {/* Added true for USD conversion */}
                            </span>
                            </div>
                        ) : (
                            <span className="text-xl font-bold text-slate-700">
                            {formatPrice(price, true)} {/* Added true for USD conversion */}
                            </span>
                        )}

                        <button
                            disabled={!isAvailable || btnLoader}
                            onClick={() => addToCartHandler({
                                image,
                                productName,
                                description,
                                specialPrice,
                                price,
                                productId,
                                quantity,
                            })}
                            className={`bg-blue-500 ${isAvailable ? "opacity-100 hover:bg-blue-600" : "opacity-70"}
                                text-white py-2 px-3 rounded-lg items-center transition-colors duration-300 w-36 flex justify-center`}
                        >
                            <FaShoppingCart className="mr-2"/>
                            {isAvailable ? "Add to Cart" : "Stock Out"}
                        </button>
                    </div>
                )}

            </div>

            <ProductViewModal 
                open={openProductViewModal}
                setOpen={setOpenProductViewModal}
                product={selectedViewProduct}
                isAvailable={isAvailable}
            />
        </div>
    )
};

export default ProductCard;
