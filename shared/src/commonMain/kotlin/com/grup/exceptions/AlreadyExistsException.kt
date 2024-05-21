import com.grup.exceptions.APIException

class AlreadyExistsException(
    override val message: String? = "Entity already exist"
) : APIException(message)