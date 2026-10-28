import { HiOutlineTrash } from "react-icons/hi";
import SetQuantity from "./SetQuantity";
import { useDispatch } from "react-redux";
import { decreaseCartQuantity, increaseCartQuantity, removeFromCart } from "../../store/actions";
import toast from "react-hot-toast";
import { formatPrice } from "../../utils/formatPrice";
import truncateText from "../../utils/truncateText";

const ItemContent = ({
    productId,
    productName,
    image,
    description,
    quantity,
    price,
    discount,
    specialPrice,
    cartId,
}) => {
    const dispatch = useDispatch();

    const itemPayload = {
        image,
        productName,
        description,
        specialPrice,
        price,
        productId,
        quantity,
        cartId,
    };

    const handleQtyIncrease = () => {
        dispatch(increaseCartQuantity(
            itemPayload,
            toast,
            quantity + 1
        ));
    };

    const handleQtyDecrease = () => {
        if (quantity > 1) {
            dispatch(decreaseCartQuantity(
                itemPayload,
                quantity - 1
            ));
        }
    };

    const removeItemFromCart = () => {
        dispatch(removeFromCart(itemPayload, toast));
    };

    return (
        <div className="grid md:grid-cols-5 grid-cols-4 md:text-md text-sm gap-4 items-center border border-slate-200 rounded-md lg:px-4 py-4 p-2 mb-3">
            <div className="md:col-span-2 justify-self-start flex flex-col gap-2">
                <div className="flex md:flex-row flex-col lg:gap-4 sm:gap-3 gap-0 items-start">
                    <h3 className="lg:text-[17px] text-sm font-semibold text-slate-600">
                        {truncateText(productName)}
                    </h3>
                </div>

                <div className="md:w-36 sm:w-24 w-12">
                    <img 
                        src={
                            image?.startsWith("http")
                                ? image
                                : `${import.meta.env.VITE_BACK_END_URL}/images/${image}`
                        }
                        alt={productName}
                        className="md:h-36 sm:h-24 h-12 w-full object-cover rounded-md"
                        onError={(e) => {
                            e.target.src = "https://placehold.co/150x150?text=No+Image";
                        }}
                    />
                </div>

                <div className="flex items-start gap-5 mt-3">
                    <button
                        onClick={removeItemFromCart}
                        className="flex items-center font-semibold space-x-2 px-4 py-1 text-xs border border-rose-600 text-rose-600 rounded-md hover:bg-red-50 transition-colors duration-200"
                    >
                        <HiOutlineTrash size={16} className="text-rose-600"/>
                        <span>Remove</span>
                    </button>
                </div>
            </div>

            {/* Price Column */}
            <div className="justify-self-center lg:text-[17px] text-sm text-slate-600 font-semibold">
                {formatPrice(Number(specialPrice || price || 0))}
            </div>

            {/* Quantity Column */}
            <div className="justify-self-center">
                <SetQuantity 
                    quantity={quantity}
                    cardCounter={true}
                    handeQtyIncrease={handleQtyIncrease}
                    handleQtyDecrease={handleQtyDecrease}
                />
            </div>

            {/* Total Item Price Column */}
            <div className="justify-self-center lg:text-[17px] text-sm text-slate-600 font-semibold">
                {formatPrice(Number(quantity) * Number(specialPrice || price || 0))}
            </div>
        </div>
    );
};

export default ItemContent;